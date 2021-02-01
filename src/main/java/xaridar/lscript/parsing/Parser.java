package xaridar.lscript.parsing;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import com.sun.xml.internal.bind.v2.model.core.ID;
import xaridar.lscript.Constants;
import xaridar.lscript.TokenEnum;
import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.interpreting.types.Value;
import xaridar.lscript.lexing.Position;
import xaridar.lscript.lexing.Token;
import xaridar.lscript.parsing.nodes.*;

import javax.management.ImmutableDescriptor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static xaridar.lscript.TokenEnum.*;

/**
 * Creates different types of nodes from a list of tokens for interpretation
 */
public class Parser {

    private Token currentToken;
    private Token nextToken;
    private final List<Token> tokens;
    private int tokenIndex;


    /**
     * Default constructor, which takes a list of tokens and sets default variable values.
     * @param tokens - a list of lexed tokens
     */
    public Parser(List<Token> tokens) {
        this.currentToken = null;
        this.nextToken = tokens.get(0);
        this.tokens = tokens;
        this.tokenIndex = -1;
        advance();
    }

    /**
     * Reverses the current token by a certain number of tokens.
     * @param num - the number of tokens back to move.
     * @return the current token.
     */
    public Token reverse(int num) {
        tokenIndex -= num;
        updateTokens();
        return currentToken;
    }

    /**
     * Sets the currentToken and nextToken variables properly based on the tokenIndex.
     */
    public void updateTokens() {
        if (tokenIndex < tokens.size()) {
            currentToken = tokens.get(tokenIndex);
            if (tokenIndex < tokens.size() - 1)
                nextToken = tokens.get(tokenIndex + 1);
            else
                nextToken = null;
        }
    }

    /**
     * Recursively parses a list of tokens using a series of methods.
     * @return a ParseResult containing the results of the parsing, in the form of either an Error or a Node.
     */
    public ParseResult parse() {
        if (currentToken.getType() != TT_EOF) {
            ParseResult res = statements();
            if (!res.hasError() && !currentToken.getType().equals(TT_EOF))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected ';', '+', '-', '*', '/', '^', '%', '==', '!', '!=', '<', '>', '<=', '>=', '+=', '-=', '*=', '/=', '&', or '|'"));
            return res;
        }
        return new ParseResult().success(new MultilineNode(new ArrayList<>(), currentToken.getPosStart().copy(), currentToken.getPosEnd().copy()));
    }

    /**
     * Advances the current token of the parser.
     * @return the current token.
     */
    public Token advance() {
        tokenIndex++;
        updateTokens();
        return currentToken;
    }

    /**
     * Represents the lowest level of nested node.
     * @return a ParseResult holding either a Node or Error - the Node can be of type NumberNode, StringNode, VarAccessNode,
     * ConditionalNode, ListNode, MapNode, ForNode, WhileNode, FuncDefNode, or a contained set of parentheses.
     */
    public ParseResult atom() {
        ParseResult res = new ParseResult();
        Token tok = currentToken;
        if (Arrays.asList(TT_INT, TT_FLOAT).contains(tok.getType())) {
            res.registerAdvancement();
            advance();
            return res.success(new NumberNode(tok));
        } else if (tok.getType().equals(TT_STR)) {
            res.registerAdvancement();
            advance();
            return res.success(new StringNode(tok));
        } else if (tok.getType().equals(TT_IDENTIFIER)) {
            res.registerAdvancement();
            advance();
            if (currentToken.getType() == TT_DOT) {
                res.registerAdvancement();
                advance();
                List<Token> s = new ArrayList<>();
                s.add(tok);
                Token varTok = currentToken;
                res.registerAdvancement();
                advance();
                while (currentToken.getType() == TT_DOT) {
                    res.registerAdvancement();
                    advance();
                    s.add(varTok);
                    varTok = currentToken;
                    res.registerAdvancement();
                    advance();
                }
                return res.success(new VarAccessNode(s, varTok));
            }
            return res.success(new VarAccessNode(tok));
        } else if (tok.getType().equals(TT_LEFT_PAREN)) {
            res.registerAdvancement();
            advance();
            Node expr = res.register(expression());
            if (res.hasError()) return res;
            if (currentToken.getType().equals(TT_RIGHT_PAREN)) {
                res.registerAdvancement();
                advance();
                return res.success(expr);
            }
            else return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));
        } else if (tok.matches(TT_KW, "if")) {
            Node ifExpr = res.register(ifExpr());
            if (res.hasError()) return res;
            return res.success(ifExpr);
        } else if (tok.getType().equals(TT_LEFT_BRACKET)) {
            Node listExpr = res.register(listExpr());
            if (res.hasError()) return res;
            return res.success(listExpr);
        } else if (tok.getType().equals(TT_LEFT_BRACE)) {
            Node mapExpr = res.register(mapExpr());
            if (res.hasError()) return res;
            return res.success(mapExpr);
        } else if (tok.matches(TT_KW, "for")) {
            Node forExpr = res.register(forExpr());
            if (res.hasError()) return res;
            return res.success(forExpr);
        } else if (tok.matches(TT_KW, "while")) {
            Node whileExpr = res.register(whileExpr());
            if (res.hasError()) return res;
            return res.success(whileExpr);
        } else if (tok.matches(TT_KW, "func") || tok.getType() == TT_KW && Constants.getInstance().MODS_LIST.contains((String) tok.getValue())) {
            Node funcDef = res.register(funcDef());
            if (res.hasError()) return res;
            return res.success(funcDef);
        } else if (currentToken.matches(TT_KW, "from")) {
            Node importNode = res.register(importFrom());
            if (res.hasError()) return res;
            return res.success(importNode);
        } else if (currentToken.matches(TT_KW, "import")) {
            Node importNode = res.register(importLine());
            if (res.hasError()) return res;
            return res.success(importNode);
        } else if (currentToken.matches(TT_KW, "new")) {
            Node instanceNode = res.register(instance());
            if (res.hasError()) return res;
            return res.success(instanceNode);
        }

        return res.slightFailure(new Error.InvalidSyntaxError(tok.getPosStart(), tok.getPosEnd(), "Expected value, identifier, '+', '-', '(', '[', '{', 'if', 'for', 'while', or 'func'"));
    }

    /**
     * Parses a new instance of a class, starting with 'new'.
     * @return a ParseResult holding either a Node or Error - the Node is of type InstanceNode
     */
    private ParseResult instance() {
        ParseResult res = new ParseResult();
        if (!currentToken.matches(TT_KW, "new"))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'new'"));
        res.registerAdvancement();
        advance();

        if (currentToken.getType() != TT_IDENTIFIER)
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected class name"));
        Token name = currentToken;
        res.registerAdvancement();
        advance();


        if (currentToken.getType() != TT_LEFT_PAREN)
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));
        res.registerAdvancement();
        advance();
        List<Node> args = new ArrayList<>();
        if (currentToken.getType() != TT_RIGHT_PAREN) {
            args.add(res.register(expression()));
            if (res.hasError()) return res;

            while (currentToken.getType() == TT_COMMA) {
                res.registerAdvancement();
                advance();

                args.add(res.register(expression()));
                if (res.hasError()) return res;
            }
        }
        if (currentToken.getType() != TT_RIGHT_PAREN)
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));
        res.registerAdvancement();
        advance();

        return res.success(new InstanceNode(name, args));
    }

    /**
     * Parses an import statement after 'import'.
     * @return a ParseResult holding either a Node or Error - the Node is of type FileImportNode
     */
    private ParseResult importLine() {
        ParseResult res = new ParseResult();
        if (!currentToken.matches(TT_KW, "import"))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'import'"));
        res.registerAdvancement();
        advance();

        if (currentToken.getType() != TT_STR) {
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected string"));
        }
        Token importString = currentToken;
        res.registerAdvancement();
        advance();

        String name = null;
        if (currentToken.matches(TT_KW, "as")) {
            res.registerAdvancement();
            advance();
            if (currentToken.getType() != TT_IDENTIFIER) {
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
            }
            name = currentToken.getValue().toString();
            res.registerAdvancement();
            advance();
        }

        return res.success(new FileImportNode(importString, name));
    }

    /**
     * Parses a list of import tokens after 'from'.
     * @return a ParseResult holding either a Node or Error - the Node is of type ImportNode
     */
    private ParseResult importFrom() {
        ParseResult res = new ParseResult();
        if (!currentToken.matches(TT_KW, "from"))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'from'"));
        res.registerAdvancement();
        advance();

        if (currentToken.getType() != TT_STR) {
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected string"));
        }
        Token importString = currentToken;
        res.registerAdvancement();
        advance();

        if (!currentToken.matches(TT_KW, "import"))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'import'"));
        res.registerAdvancement();
        advance();

        List<Token> toImport = new ArrayList<>();
        if (currentToken.getType() != TT_IDENTIFIER) {
            if (currentToken.getType() == TT_MUL) {
                res.registerAdvancement();
                advance();
                List<String> names = Collections.singletonList(importString.getValue().toString().split("[./]")[importString.getValue().toString().split("[./]").length - 1]);
                return res.success(new ImportNode(importString, null, names));
            } else return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
        }
        toImport.add(currentToken);
        res.registerAdvancement();
        advance();

        while (currentToken.getType() == TT_COMMA) {
            res.registerAdvancement();
            advance();
            if (currentToken.getType() != TT_IDENTIFIER) {
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
            }
            toImport.add(currentToken);
            res.registerAdvancement();
            advance();
        }
        List<String> names = null;
        if (currentToken.matches(TT_KW, "as")) {
            res.registerAdvancement();
            advance();
            names = new ArrayList<>();
            if (currentToken.getType() != TT_IDENTIFIER) {
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
            }
            names.add(currentToken.getValue().toString());
            res.registerAdvancement();
            advance();

            while (currentToken.getType() == TT_COMMA) {
                res.registerAdvancement();
                advance();
                if (currentToken.getType() != TT_IDENTIFIER) {
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
                }
                names.add(currentToken.getValue().toString());
                res.registerAdvancement();
                advance();
            }
        }
        if (names == null) {
            names = toImport.stream().map(token -> token.getValue().toString()).collect(Collectors.toList());
        }
        if (names.size() != toImport.size()) {
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected " + toImport.size() + "names, got " + names.size()));
        }
        return res.success(new ImportNode(importString, toImport, names));
    }

    /**
     * Parses a map after a '{' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type MapNode
     */
    private ParseResult mapExpr() {
        ParseResult res = new ParseResult();
        Position posStart = currentToken.getPosStart().copy();
        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));
        res.registerAdvancement();
        advance();

        List<Tuple<Node, Node>> pairs = new ArrayList<>();

        if (!currentToken.getType().equals(TT_RIGHT_BRACE)) {
            Node key = res.register(expression());
            if (res.hasError())
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected '}', type, variable, identifier, '+', '-', or '('"));

            if (!currentToken.getType().equals(TT_COLON))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected ':'"));
            res.registerAdvancement();
            advance();

            Node value = res.register(expression());
            if (res.hasError())
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected type, variable, identifier, '+', '-', or '('"));

            pairs.add(Tuple.of(key, value));
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                key = res.register(expression());
                if (res.hasError()) return res;

                if (!currentToken.getType().equals(TT_COLON))
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                            "Expected ':'"));
                res.registerAdvancement();
                advance();

                value = res.register(expression());
                if (res.hasError()) return res;
                pairs.add(Tuple.of(key, value));
            }
            if (!currentToken.getType().equals(TT_RIGHT_BRACE))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ',' or '}'"));
        }
        res.registerAdvancement();
        advance();
        return res.success(new MapNode(pairs, posStart, currentToken.getPosEnd().copy()));
    }

    /**
     * Parses a list after a '[' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type ListNode
     */
    private ParseResult listExpr() {
        ParseResult res = new ParseResult();
        Position posStart = currentToken.getPosStart().copy();
        if (!currentToken.getType().equals(TT_LEFT_BRACKET))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '['"));
        res.registerAdvancement();
        advance();

        List<Node> elements = new ArrayList<>();

        if (!currentToken.getType().equals(TT_RIGHT_BRACKET)) {
            elements.add(res.register(expression()));
            if (res.hasError())
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected ']', 'for', 'if', 'while', 'func', type, variable, identifier, '+', '-', or '('"));
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                elements.add(res.register(expression()));
                if (res.hasError()) return res;
            }
            if (!currentToken.getType().equals(TT_RIGHT_BRACKET))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ',' or ']'"));

        }
        res.registerAdvancement();
        advance();
        return res.success(new ListNode(elements, posStart, currentToken.getPosEnd().copy()));
    }

    /**
     * Parses a function definition after a 'func' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type FuncDefNode
     */
    private ParseResult funcDef() {
        ParseResult res = new ParseResult();
        ModifierList mods = new ModifierList();
        if (!currentToken.getType().equals(TT_KW)) {
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected keyword (variable modifier or 'func')"));
        }
        while (!currentToken.matches(TT_KW, "func")) {
            String val = (String) currentToken.getValue();
            String err = mods.addModByString(val);
            if (err != null) {
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosEnd(), currentToken.getPosEnd(), err));
            }
            res.registerAdvancement();
            advance();
        }
        mods.setToDefaults();
        res.registerAdvancement();
        advance();
        Token varNameToken;
        if (currentToken.getType().equals(TT_IDENTIFIER)) {
            varNameToken = currentToken;
            res.registerAdvancement();
            advance();

            if (!currentToken.getType().equals(TT_LEFT_PAREN))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));
        } else {
            varNameToken = null;
            if (!currentToken.getType().equals(TT_LEFT_PAREN))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected function identifier or '('"));
        }

        res.registerAdvancement();
        advance();

        List<Tuple<Token, Token>> argNameTokens = new ArrayList<>();

        if (currentToken.getType().equals(TT_IDENTIFIER)) {
            Token type = currentToken;
            res.registerAdvancement();
            advance();
            if (!currentToken.getType().equals(TT_IDENTIFIER))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
            argNameTokens.add(Tuple.of(type, currentToken));
            res.registerAdvancement();
            advance();

            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();
                if (!currentToken.getType().equals(TT_IDENTIFIER))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected variable type"));
                type = currentToken;
                res.registerAdvancement();
                advance();
                if (!currentToken.getType().equals(TT_IDENTIFIER))
                    return res.failure(
                            new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
                argNameTokens.add(Tuple.of(type, currentToken));
                res.registerAdvancement();
                advance();
            }

            if (!currentToken.getType().equals(TT_RIGHT_PAREN))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ',' or ')'"));
        }

        else {
            if (!currentToken.getType().equals(TT_RIGHT_PAREN))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'variable type' or ')'"));
        }

        res.registerAdvancement();
        advance();
        List<String> returnTypes = new ArrayList<>();

        if(currentToken.getType().equals(TT_COLON)) {
            res.registerAdvancement();
            advance();
            if (!currentToken.getType().equals(TT_IDENTIFIER))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
            returnTypes.add((String) currentToken.getValue());
            res.registerAdvancement();
            advance();
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                if (!currentToken.getType().equals(TT_IDENTIFIER))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                returnTypes.add(((String) currentToken.getValue()));
                res.registerAdvancement();
                advance();
            }
        }

        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{' or ':'"));

        res.registerAdvancement();
        advance();

        Node nodeToReturn = res.register(statements());
        if (res.hasError()) return res;

        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();
        if (!(nodeToReturn instanceof MultilineNode))
            return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'multiline statement in function definition'"));
        return res.success(new FuncDefNode(varNameToken, argNameTokens, returnTypes, (MultilineNode) nodeToReturn, mods));
    }

    /**
     * Parses a while loop after a 'while' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type WhileNode
     */
    private ParseResult whileExpr() {
        ParseResult res = new ParseResult();
        if (!currentToken.matches(TT_KW, "while"))
            return res.failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'while'"));

        res.registerAdvancement();
        advance();
        if (!currentToken.getType().equals(TT_LEFT_PAREN))
            return res.failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));

        res.registerAdvancement();
        advance();

        Node condition = res.register(expression());
        if (res.hasError()) return res;
        if (!currentToken.getType().equals(TT_RIGHT_PAREN))
            return res.failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return res.failure(
                new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));

        res.registerAdvancement();
        advance();

        Node body = res.register(statements());
        if (res.hasError()) return res;
        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return res.failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();

        return res.success(new WhileNode(condition, body));
    }

    /**
     * Parses a for loop after a 'for' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type ForNode
     */
    private ParseResult forExpr() {
        ParseResult res = new ParseResult();
        Node steps = null;
        if (!currentToken.matches(TT_KW, "for"))
        return res.failure(
                new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'for'"));

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_LEFT_PAREN)) {
            return new ParseResult().failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));
        }
        res.registerAdvancement();
        advance();

        if (!currentToken.matches(TT_KW, "int") && !currentToken.matches(TT_KW, "var"))
            return res.failure(
                new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected variable type"));

        Token type = currentToken;

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_IDENTIFIER))
            return res.failure(
                new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));

        Token startIdToken = currentToken;

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_EQ))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '='"));

        res.registerAdvancement();
        advance();

        Node startVal = res.register(expression());
        if (res.hasError()) return res;

        if (!currentToken.getType().equals(TT_COMMA))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ','"));

        res.registerAdvancement();
        advance();

        Node endVal = res.register(expression());
        if (res.hasError()) return res;

        if (currentToken.getType().equals(TT_COMMA)) {
            res.registerAdvancement();
            advance();

            steps = res.register(expression());
            if (res.hasError()) return res;
        }


        if (!currentToken.getType().equals(TT_RIGHT_PAREN))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                    "Expected ')'"));
        res.registerAdvancement();
        advance();


        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));

        res.registerAdvancement();
        advance();

        Node body = res.register(statements());
        if (res.hasError()) return res;
        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();


        return res.success(new ForNode(type, startIdToken, startVal, endVal, steps, body));
    }

    /**
     * Parses a conditional after an 'if' token is detected.
     * @return a ParseResult holding either a Node or Error - the Node is of type ConditionalNode
     */
    private ParseResult ifExpr() {
        ParseResult res = new ParseResult();
        Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> allCases = ifExprCases("if");
        if (allCases.getRight() != null) return res.failure(allCases.getRight());

        return res.success(new ConditionalNode(allCases.getLeft().getLeft(), allCases.getLeft().getRight(), true));
    }

    /**
     * Parses a conditional case after a 'if' or 'elif' token is detected.
     * @param caseKW - the keyword of the last token (either 'if' or 'elif').
     * @return a nested list of tuples holding all of the cases of a conditional statement.
     */
    private Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprCases(String caseKW) {
        ParseResult res = new ParseResult();
        List<Tuple<Tuple<Node, Node>, Boolean>> cases = new ArrayList<>();
        Tuple<Node, Boolean> elseCase;

        if (!currentToken.matches(TT_KW, caseKW))
            return Tuple.of(null,
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'if'"));

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_LEFT_PAREN))
            return Tuple.of(null,
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));
        res.registerAdvancement();
        advance();

        Node boolExpr = res.register(expression());
        if (res.hasError()) return Tuple.of(null, res.getError());

        if (!currentToken.getType().equals(TT_RIGHT_PAREN))
            return Tuple.of(null,
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));
        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return Tuple.of(null, new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));

        res.registerAdvancement();
        advance();

        Node statements = res.register(statements());
        if (res.hasError()) return Tuple.of(null, res.getError());
        cases.add(Tuple.of(Tuple.of(boolExpr, statements), true));

        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return Tuple.of(null,
                        new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();
        Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> allCases = ifExprBorC();
        if (res.hasError()) return Tuple.of(null, res.getError());
        cases.addAll(allCases.getLeft().getLeft());
        elseCase = allCases.getLeft().getRight();
        return Tuple.of(Tuple.of(cases, elseCase), null);
    }

    /**
     * Parses either an 'elif' or 'else' token for o ConditionalNode.
     * @return a Tuple holding all conditional cases at this point in parsing.
     */
    public Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprBorC() {
        List<Tuple<Tuple<Node, Node>, Boolean>> cases = new ArrayList<>();
        Tuple<Node, Boolean> elseCase;

        if (currentToken.matches(TT_KW, "elif")) {
            Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> allCases = ifExprB();
            if (allCases.getRight() != null) return Tuple.of(null, allCases.getRight());
            cases = allCases.getLeft().getLeft();
            elseCase = allCases.getLeft().getRight();
        } else {
            Tuple<Tuple<Node, Boolean>, Error> elseRes = ifExprC();
            if (elseRes.getRight() != null) return Tuple.of(null, elseRes.getRight());
            elseCase = elseRes.getLeft();
        }

        return Tuple.of(Tuple.of(cases, elseCase), null);
    }

    /**
     * Parses a conditional after an 'elif' token is detected.
     * @return a List of nested tokens holding the conditional statement at this point in parsing.
     */
    public Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprB() {
        return ifExprCases("elif");
    }

    /**
     * Parses a conditional after an 'else' token is detected.
     * @return a List of nested tokens holding the else case of a conditional.
     */
    public Tuple<Tuple<Node, Boolean>, Error> ifExprC() {
        ParseResult res = new ParseResult();
        Tuple<Node, Boolean> elseCase = null;

        if (currentToken.matches(TT_KW, "else")) {
            res.registerAdvancement();
            advance();

            if (!currentToken.getType().equals(TT_LEFT_BRACE))
                return Tuple.of(null, new Error.ExpectedCharError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

            res.registerAdvancement();
            advance();

            Node statements = res.register(statements());
            if (res.hasError()) return Tuple.of(null, res.getError());
            if (!currentToken.getType().equals(TT_RIGHT_BRACE))
                return Tuple.of(null, new Error.ExpectedCharError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));

            res.registerAdvancement();
            advance();
            elseCase = Tuple.of(statements, true);
        }
        return Tuple.of(elseCase, null);
    }

    /**
     * Represents a level of nested node that checks for a power operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult power() {
        return binaryOperation(unused -> call(), Collections.singletonList(TT_POW), (unused -> factor()));
    }

    /**
     * Represents a level of nested node that checks for a function call.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult call() {
        ParseResult res = new ParseResult();
        Node atom = res.register(atom());
        if (res.hasError()) return res;

        if (currentToken.getType().equals(TT_LEFT_PAREN)) {
            res.registerAdvancement();
            advance();
            List<Node> argNodes = new ArrayList<>();

            if (!currentToken.getType().equals(TT_RIGHT_PAREN)) {
                argNodes.add(res.register(expression()));
                if (res.hasError())
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                            "Expected ')', 'for', 'if', 'while', 'func', type, value, identifier, '+', '-', '('"));
                while (currentToken.getType().equals(TT_COMMA)) {
                    res.registerAdvancement();
                    advance();

                    argNodes.add(res.register(expression()));
                    if (res.hasError()) return res;
                }
                if (!currentToken.getType().equals(TT_RIGHT_PAREN))
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ',' or ')'"));

            }
            res.registerAdvancement();
            advance();
            if (atom instanceof VarAccessNode)
                return res.success(new CallNode((VarAccessNode) atom, argNodes));
            return res.failure(new Error.InvalidSyntaxError(atom.getPosStart(), atom.getPosEnd(), "Expected identifier"));
        }
        return res.success(atom);
    }

    /**
     * Represents a level of nested node that checks for a unary operation negativity modifier on a Number.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult factor() {
        ParseResult res = new ParseResult();
        Token tok = currentToken;

        if (Arrays.asList(TT_PLUS, TT_MINUS).contains(tok.getType())) {
            res.registerAdvancement();
            advance();
            Node factor = res.register(factor());
            if (res.hasError()) return res;
            return res.success(new UnaryOperationNode(tok, factor));
        }

        return power();
    }

    /**
     * Represents a level of nested node that checks for a multiplication, division, or modulo operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult term() {
        return binaryOperation(unused -> factor(), Arrays.asList(TT_MUL, TT_DIV, TT_MOD), null);
    }

    /**
     * Represents a level of nested node that holds a list of semicolon-separated nodes.
     * @return a ParseResult holding either a Node or Error - the Node is of type ListNode.
     */
    public ParseResult statements() {
        ParseResult res = new ParseResult();
        List<Node> statements = new ArrayList<>();
        Position posStart = currentToken.getPosStart().copy();

        Node statement = res.register(statement());
        if (res.hasError()) return res;
        statements.add(statement);

        if (!currentToken.getType().equals(TT_SEMICOLON) && statement.requiresSemicolon()) {
            return res.failure(new Error.ExpectedCharError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ';'"));
        }
        if (statement.requiresSemicolon()) {
            res.registerAdvancement();
            advance();
        }

        while (true) {
            statement = res.tryRegister(statement());
            if (statement != null) {
                statements.add(statement);
                if (!currentToken.getType().equals(TT_SEMICOLON) && statement.requiresSemicolon()) {
                    return res.failure(new Error.ExpectedCharError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ';'"));
                }

                if (statement.requiresSemicolon()) {
                    res.registerAdvancement();
                    advance();
                }
            } else {
                Error err = res.pullError();
                if (err != null) {
                    return res.failure(err);
                } else {
                    reverse(res.getToReverseCount());

                    break;
                }
            }
        }
        return res.success(new MultilineNode(statements, posStart, currentToken.getPosEnd().copy()));
    }

    /**
     * Represents a level of nested node that parses a single line, ending with a semicolon. This method checks for 'return', 'break', and 'continue' keywords.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult statement() {
        ParseResult res = new ParseResult();
        Position posStart = currentToken.getPosStart().copy();

        if (currentToken.matches(TT_KW, "return")) {
            res.registerAdvancement();
            advance();
            List<Node> nodesToReturn = new ArrayList<>();

            Node node = res.tryRegister(expression());
            if (node == null) reverse(res.getToReverseCount());
            if (node != null) {
                nodesToReturn.add(node);
            }
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                node = res.tryRegister(expression());
                if (node == null) reverse(res.getToReverseCount());
                nodesToReturn.add(node);
            }
            return res.success(new ReturnNode(nodesToReturn, posStart, currentToken.getPosStart().copy()));
        }
        if (currentToken.matches(TT_KW, "continue")) {
            res.registerAdvancement();
            advance();
            return res.success(new ContinueNode(posStart, currentToken.getPosStart().copy()));
        }
        if (currentToken.matches(TT_KW, "break")) {
            res.registerAdvancement();
            advance();
            return res.success(new BreakNode(posStart, currentToken.getPosStart().copy()));
        }
        Node expression = res.register(expression());
        if (res.hasError())
            return res.slightFailure(new Error.InvalidSyntaxError(posStart, currentToken.getPosEnd(), "Expected type, 'return', 'continue', 'break', "));
        return res.success(expression);
    }

    /**
     * Represents a level of nested node that checks for an inline conditional operator or index operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult expression() {
        ParseResult res = new ParseResult();
        List<Tuple<Tuple<Node, Node>, Boolean>> cases = new ArrayList<>();
        Node left = res.register(expr());
        if (res.hasError()) return res;
        if (currentToken.getType().equals(TT_QUESTION)) {
            res.registerAdvancement();
            advance();

            Node aCase = res.register(expr());
            if (res.hasError()) return res;
            cases.add(Tuple.of(Tuple.of(left, aCase), false));

            if (!currentToken.getType().equals(TT_COLON))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ':'"));

            res.registerAdvancement();
            advance();

            Tuple<Node, Boolean> elseCase = Tuple.of(res.register(expression()), false);
            if (res.hasError()) return res;
            return res.success(new ConditionalNode(cases, elseCase));
        } else if (currentToken.getType().equals(TT_LEFT_BRACKET)) {
            res.registerAdvancement();
            advance();
            Node startIdx = null;
            Node endIdx = null;
            if (currentToken.getType() == TT_COLON) {
                res.registerAdvancement();
                advance();
                endIdx = res.register(expr());
                if (res.hasError()) return res;
            } else {
                startIdx = res.register(expr());
                if (res.hasError()) return res;
                if (currentToken.getType() == TT_COLON) {
                    res.registerAdvancement();
                    advance();
                    if (currentToken.getType() != TT_RIGHT_BRACKET) {
                        endIdx = res.register(expr());
                        if (res.hasError()) return res;
                    }
                } else {
                    endIdx = startIdx;
                }
            }



            if (!currentToken.getType().equals(TT_RIGHT_BRACKET))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ']'"));

            res.registerAdvancement();
            advance();

            if (currentToken.getType().equals(TT_EQ)) {
                res.registerAdvancement();
                advance();

                Node toSet = res.register(expr());
                if (res.hasError()) return res;
                return res.success(new SetIndexNode(left, startIdx, endIdx, toSet));
            }

            return res.success(new IndexNode(left, startIdx, endIdx));
        }

        return res.success(left);
    }

    /**
     * Represents a level of nested node that checks for variable assignment, as well as for boolean operators & and |.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult expr() {
        ParseResult res = new ParseResult();
        ModifierList mods = new ModifierList();
        int modNum = 0;
        if (currentToken.getType().equals(TT_KW) && Constants.getInstance().MODS_LIST.contains(currentToken.getValue().toString())) {
            String val;
            while (Constants.getInstance().MODS_LIST.contains(currentToken.getValue().toString())) {
                if (currentToken.getType() != TT_KW) {
                    break;
                }
                val = (String) currentToken.getValue();
                String err = mods.addModByString(val);
                if (err != null) {
                    if (err.equals("")) {
                        if (currentToken.getType().equals(TT_IDENTIFIER)) reverse(modNum);
                        break;
                    }
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosEnd(), currentToken.getPosEnd(), err));
                }
                res.registerAdvancement();
                advance();
                modNum++;
            }
        }
        mods.setToDefaults();
        if (currentToken.getType().equals(TT_IDENTIFIER)) {
            List<Token> s = new ArrayList<>();
            Token type = null;
            Token varName;
            if (!currentToken.getType().equals(TT_IDENTIFIER))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));
            if (modNum > 0) {
                type = currentToken;
                res.registerAdvancement();
                advance();

                if (!currentToken.getType().equals(TT_IDENTIFIER))
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));

                varName = currentToken;
                res.registerAdvancement();
                advance();
            } else {
                varName = currentToken;
                if (nextToken.getType().equals(TT_IDENTIFIER)) {
                    res.registerAdvancement();
                    advance();
                    type = varName;
                    varName = currentToken;

                    res.registerAdvancement();
                    advance();
                } else {
                    res.registerAdvancement(); advance();
                    if (currentToken.getType() == TT_DOT) {
                        res.registerAdvancement();
                        advance();
                        s.add(varName);
                        varName = currentToken;
                        res.registerAdvancement();
                        advance();
                        while (currentToken.getType() == TT_DOT) {
                            res.registerAdvancement();
                            advance();
                            s.add(varName);
                            varName = currentToken;
                            res.registerAdvancement();
                            advance();
                        }
                    }
                    reverse(1);
                }
            }
            String typeStr = "";
            if (type != null) typeStr = type.getValue().toString();
            if (Constants.getInstance().TYPES_BRACKET.contains(typeStr)) {
                if (currentToken.getType() == TT_LEFT_BRACE) {
                    Position posStart = currentToken.getPosStart().copy();
                    res.registerAdvancement();
                    advance();
                    MultilineNode statements = (MultilineNode) res.register(statements());
                    if (res.hasError()) return res;
                    List<VarNode> varAssignNodes = new ArrayList<>();
                    List<FuncDefNode> funcDefNodes = new ArrayList<>();
                    FuncDefNode constructor = null;
                    for (Node node : statements.getNodes()) {
                        if (node instanceof VarNode) {
                            varAssignNodes.add((VarNode) node);
                        } else if (node instanceof FuncDefNode) {
                            if (constructor == null && ((FuncDefNode) node).getReturnTypes().size() == 0 && ((FuncDefNode) node).getVarNameToken().matches(TT_IDENTIFIER, "constructor")) {
                                constructor = (FuncDefNode) node;
                            } else {
                                funcDefNodes.add((FuncDefNode) node);
                            }
                        } else {
                            return res.failure(new Error.InvalidSyntaxError(node.getPosStart(), node.getPosEnd(), "Expected methods, variables, and/or inner classes in class."));
                        }
                    }
                    if (currentToken.getType() != TT_RIGHT_BRACE) return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));
                    res.registerAdvancement();
                    advance();
                    return res.success(new ClassNode(varName, varAssignNodes, funcDefNodes, constructor, posStart, currentToken.getPosEnd().copy(), mods));
                }
            }

            if (type != null) {
                boolean list = false;
                boolean allSameType = false;
                List<Tuple<Token, Token>> vars = new ArrayList<>();
                vars.add(Tuple.of(type, varName));
                if (currentToken.getType().equals(TT_COMMA)) {
                    list = true;
                    while (currentToken.getType().equals(TT_COMMA)) {
                        res.registerAdvancement();
                        advance();
                        if (!currentToken.getType().equals(TT_IDENTIFIER))
                            allSameType = true;
                        if (!allSameType) {
                            if (!currentToken.getType().equals(TT_IDENTIFIER))
                                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                            type = currentToken;
                            res.registerAdvancement();
                            advance();
                        }

                        if (!currentToken.getType().equals(TT_IDENTIFIER))
                            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));

                        vars.add(Tuple.of(type, currentToken));
                        res.registerAdvancement();
                        advance();
                    }
                }
                if (currentToken.getType().equals(TT_EQ)) {
                    res.registerAdvancement();
                    advance();
                    if (list) {
                        Node valListNode = res.register(valList());
                        if (res.hasError()) return res;
                        return res.success(new VarListAssignNode(vars, valListNode, allSameType));
                    } else {
                        Node expression = res.register(expression());
                        if (res.hasError()) return res;
                        return res.success(new VarAssignNode(type, varName, expression, mods));
                    }
                } else if (currentToken.getType().equals(TT_SEMICOLON)) {
                    Node def = Value.getDefaultValue(((String) type.getValue()), currentToken.getPosEnd().copy());
                    if (list) {
                        def = new ListNode(vars.stream().map(tup -> Value.getDefaultValue(tup.getLeft().getValue().toString(), tup.getLeft().getPosStart())).collect(Collectors.toList()), vars.get(0).getLeft().getPosStart(), vars.get(vars.size() - 1).getRight().getPosEnd());
                        if (res.hasError()) return res;
                        return res.success(new VarListAssignNode(vars, def, allSameType));
                    }
                    return res.success(new VarAssignNode(type, varName, def, mods));
                }
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '=', ',', '{', or ';'"));
            }
            Map<TokenEnum, TokenEnum> eqMods = Constants.getInstance().EQUAL_MODS.values().stream().collect(Collectors.toMap(m -> m.get("with"), m -> m.get("without")));
            if (nextToken.getType().equals(TT_EQ)) {
                res.registerAdvancement();
                advance();
                res.registerAdvancement();
                advance();
                Node expression = res.register(expression());
                if (res.hasError()) return res;
                return res.success(new VarAssignNode(null, varName, s, expression, null));
            } else if (Arrays.asList(TT_PLUS, TT_MINUS).contains(nextToken.getType())) {
                if (tokens.size() >= tokenIndex + 2 && tokens.get(tokenIndex + 2).getType().equals(nextToken.getType())) {
                    tokens.set(tokenIndex + 2, new Token(TT_INT, 1, nextToken.getPosStart(), null, null));
                    Node assignment = res.register(expression());
                    if (res.hasError()) return res;
                    return res.success(new VarAssignNode(null, varName, s, assignment, null));
                }
            } else if (eqMods.containsKey(nextToken.getType())) {
                TokenEnum toToken = eqMods.get(nextToken.getType());
                Token tok = new Token(toToken, null, nextToken.getPosStart(), null, null);
                tokens.set(tokenIndex + 1, tok);
                nextToken = tok;
                Node assignment = res.register(expression());
                if (res.hasError()) return res;
                return res.success(new VarAssignNode(null, varName, s, assignment, null));
            }
            reverse(s.size() * 2);
        }
        Node node = res.register(binaryOperation(unused -> comp(), Arrays.asList(TT_PIPE, TT_AND), null));
        if (res.hasError()) return res.slightFailure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type, 'if', 'for', 'while', 'func', value, identifier, '+', '-', '(', '[', '{', or '!'"));
        return res.success(node);
    }

    /**
     * Represents a list of nodes.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult valList() {
        ParseResult res = new ParseResult();

        List<Node> nodes = new ArrayList<>();
        Node n = res.register(expression());
        if (res.hasError()) return res;
        nodes.add(n);
        while (currentToken.getType().equals(TT_COMMA)) {
            res.registerAdvancement();
            advance();
            n = res.register(expression());
            nodes.add(n);
            if (res.hasError()) return res;
        }
        return res.success(new ValueListNode(nodes));
    }

    /**
     * Represents a level of nested node that checks for a boolean operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult comp() {
        ParseResult res = new ParseResult();
        if (currentToken.getType().equals(TT_BANG)) {
            Token opToken = currentToken;
            res.registerAdvancement();
            advance();

            Node node = res.register(comp());
            if (res.hasError()) return res;
            return res.success(new UnaryOperationNode(opToken, node));
        }
        Node node = res.register(binaryOperation(unused -> arith(), Arrays.asList(TT_NEQ, TT_GEQ, TT_LEQ, TT_LT, TT_GT, TT_BOOLEQ), null));
        if (res.hasError()) return res.slightFailure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected value, identifier, '+', '-', '(', '[', '{', or '!'"));
        return res.success(node);
    }

    /**
     * Represents a level of nested node that checks for an addition or subtraction operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult arith() {
        return binaryOperation(usused -> term(), Arrays.asList(TT_PLUS, TT_MINUS), null);
    }

    /**
     * Checks for a binary operation between two or more nodes based on parameters.
     * @param leftFunc - the method to call on the left side of the binary operator.
     * @param ops - a List of operator tokens to check for between Nodes
     * @param rightFunc - the method to call on the right side of the binary operator.
     * @return a ParseResult holding either a Node or Error - The Node is of type BinaryOperationNode, or it passes the node on the left.
     */
    public ParseResult binaryOperation(Function<Void, ParseResult> leftFunc, List<TokenEnum> ops, Function<Void, ParseResult> rightFunc) {
        if (rightFunc == null) rightFunc = leftFunc;
        ParseResult res = new ParseResult();
        Node left = res.register(leftFunc.apply(null));
        if (res.hasError()) return res;

        while (ops.contains(currentToken.getType())) {
            Token opTok = currentToken;
            res.registerAdvancement();
            advance();
            Node right = res.register(rightFunc.apply(null));
            if (res.hasError()) return res;
            left = new BinaryOperationNode(left, opTok, right);
        }

        return res.success(left);
    }

}
