package lscript.parsing;

import lscript.Constants;
import lscript.TokenEnum;
import lscript.errors.Error;
import lscript.interpreting.types.Value;
import lscript.lexing.Position;
import lscript.Tuple;
import lscript.lexing.Token;
import lscript.parsing.nodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lscript.TokenEnum.*;

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
        return new ParseResult().success(new ListNode(new ArrayList<>(), currentToken.getPosStart(), currentToken.getPosEnd()));
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
        if (List.of(TT_INT, TT_FLOAT).contains(tok.getType())) {
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
        } else if (tok.matches(TT_KW, "func")) {
            Node funcDef = res.register(funcDef());
            if (res.hasError()) return res;
            return res.success(funcDef);
        }

        return res.failure(new Error.InvalidSyntaxError(tok.getPosStart(), tok.getPosEnd(), "Expected value, identifier, '+', '-', '(', '[', '{', 'if', 'for', 'while', or 'func'"));
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
        if (!currentToken.matches(TT_KW, "func"))
            return res.failure(
                    new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'func'"));
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

        if (currentToken.getType().equals(TT_KW)) {
            String str = (String) currentToken.getValue();
            if (!Constants.getInstance().TYPES.containsKey(str))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected variable type"));
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
                str = (String) currentToken.getValue();
                if (!Constants.getInstance().TYPES.containsKey(str))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                        "Expected variable type"));
                if (currentToken.getValue().equals("const"))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                            "Const variables not allowed as parameters"));
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
            if (!currentToken.getType().equals(TT_KW))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
            String str = (String) currentToken.getValue();
            if (!Constants.getInstance().TYPES.containsKey(str))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
            returnTypes.add((String) currentToken.getValue());
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                if (!currentToken.getType().equals(TT_KW))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                str = (String) currentToken.getValue();
                if (!Constants.getInstance().TYPES.containsKey(str))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                returnTypes.add(((String) currentToken.getValue()));
            }
            res.registerAdvancement();
            advance();
        }

        if (!currentToken.getType().equals(TT_LEFT_BRACE))
            return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));

        res.registerAdvancement();
        advance();

        Node nodeToReturn = res.register(statements());
        if (res.hasError()) return res;

        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();

        return res.success(new FuncDefNode(varNameToken, argNameTokens, returnTypes, nodeToReturn));
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

        Token start_id_tok = currentToken;

        res.registerAdvancement();
        advance();

        if (!currentToken.getType().equals(TT_EQ))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '='"));

        res.registerAdvancement();
        advance();

        Node start_val = res.register(expression());
        if (res.hasError()) return res;

        if (!currentToken.getType().equals(TT_COMMA))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ','"));

        res.registerAdvancement();
        advance();

        Node end_val = res.register(expression());
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


        return res.success(new ForNode(type, start_id_tok, start_val, end_val, steps, body));
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
        Tuple<Node, Boolean> else_case;

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

        Node bool_expr = res.register(expression());
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
        cases.add(Tuple.of(Tuple.of(bool_expr, statements), true));

        if (!currentToken.getType().equals(TT_RIGHT_BRACE))
            return Tuple.of(null,
                        new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '}'"));

        res.registerAdvancement();
        advance();
        Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> allCases = ifExprBorC();
        if (res.hasError()) return Tuple.of(null, res.getError());
        cases.addAll(allCases.getLeft().getLeft());
        else_case = allCases.getLeft().getRight();
        return Tuple.of(Tuple.of(cases, else_case), null);
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
        return bin_op(unused -> call(), List.of(TT_POW), (unused -> factor()));
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
            List<Node> arg_nodes = new ArrayList<>();

            if (!currentToken.getType().equals(TT_RIGHT_PAREN)) {
                arg_nodes.add(res.register(expression()));
                if (res.hasError())
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                            "Expected ')', 'for', 'if', 'while', 'func', type, value, identifier, '+', '-', '('"));
                while (currentToken.getType().equals(TT_COMMA)) {
                    res.registerAdvancement();
                    advance();

                    arg_nodes.add(res.register(expression()));
                    if (res.hasError()) return res;
                }
                if (!currentToken.getType().equals(TT_RIGHT_PAREN))
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ',' or ')'"));

            }
            res.registerAdvancement();
            advance();
            if (atom instanceof VarAccessNode)
                return res.success(new CallNode((VarAccessNode) atom, arg_nodes));
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

        if (List.of(TT_PLUS, TT_MINUS).contains(tok.getType())) {
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
        return bin_op(unused -> factor(), List.of(TT_MUL, TT_DIV, TT_MOD), null);
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

        res.registerAdvancement();
        advance();

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
                reverse(res.getToReverseCount());

                break;
            }
        }

        return res.success(new ListNode(statements, posStart, currentToken.getPosEnd()));
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
            return res.failure(new Error.InvalidSyntaxError(posStart, currentToken.getPosEnd(), "Expected type, 'return', 'continue', 'break', 'if', 'for', 'while', 'func', value, identifier, '+', '-', '(', '[', '{', or '!'"));
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

            Tuple<Node, Boolean> else_case = Tuple.of(res.register(expression()), false);
            if (res.hasError()) return res;
            return res.success(new ConditionalNode(cases, else_case));
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
        if (currentToken.getType().equals(TT_KW)) {
            String str = (String) currentToken.getValue();
            if (Constants.getInstance().TYPES.containsKey(str)) {
                Token type = currentToken;
                res.registerAdvancement();
                advance();

                if (!currentToken.getType().equals(TT_IDENTIFIER))
                    return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected identifier"));

                Token var_name = currentToken;
                res.registerAdvancement();
                advance();

                if (currentToken.getType().equals(TT_EQ)) {
                    res.registerAdvancement();
                    advance();
                    Node expression = res.register(expression());
                    if (res.hasError()) return res;
                    return res.success(new VarAssignNode(type, var_name, expression));
                } else if (currentToken.getType().equals(TT_SEMICOLON)) {
                    Node def = Value.getDefaultValue(((String) type.getValue()), currentToken.getPosEnd().copy());
                    return res.success(new VarAssignNode(type, var_name, def));
                }
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '=' or ';'"));
            }
        } else if (currentToken.getType().equals(TT_IDENTIFIER)) {
            Token var_name = currentToken;
            Map<TokenEnum, TokenEnum> mods = Constants.getInstance().EQUAL_MODS.values().stream().collect(Collectors.toMap(m -> m.get("with"), m -> m.get("without")));
            if (nextToken != null) {
                if (nextToken.getType().equals(TT_EQ)) {
                    res.registerAdvancement();
                    advance();
                    res.registerAdvancement();
                    advance();
                    Node expression = res.register(expression());
                    if (res.hasError()) return res;
                    return res.success(new VarAssignNode(null, var_name, expression));
                } else if (List.of(TT_PLUS, TT_MINUS).contains(nextToken.getType())) {
                    if (tokens.size() >= tokenIndex + 2 && tokens.get(tokenIndex + 2).getType().equals(nextToken.getType())) {
                        tokens.set(tokenIndex + 2, new Token(TT_INT, 1, nextToken.getPosStart(), null, null));
                        Node assignment = res.register(expression());
                        if (res.hasError()) return res;
                        return res.success(new VarAssignNode(null, var_name, assignment));
                    }
                } else if (mods.containsKey(nextToken.getType())) {
                    TokenEnum to_token = mods.get(nextToken.getType());
                    Token tok = new Token(to_token, null, nextToken.getPosStart(), null, null);
                    tokens.set(tokenIndex + 1, tok);
                    nextToken = tok;
                    Node assignment = res.register(expression());
                    if (res.hasError()) return res;
                    return res.success(new VarAssignNode(null, var_name, assignment));
                }
            }
        }
        Node node = res.register(bin_op(unused -> comp(), List.of(TT_PIPE, TT_AND), null));
        if (res.hasError()) return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type, 'if', 'for', 'while', 'func', value, identifier, '+', '-', '(', '[', '{', or '!'"));
        return res.success(node);
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
        Node node = res.register(bin_op(unused -> arith(), List.of(TT_NEQ, TT_GEQ, TT_LEQ, TT_LT, TT_GT, TT_BOOLEQ), null));
        if (res.hasError()) return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected value, identifier, '+', '-', '(', '[', '{', or '!'"));
        return res.success(node);
    }

    /**
     * Represents a level of nested node that checks for an addition or subtraction operator.
     * @return a ParseResult holding either a Node or Error.
     */
    public ParseResult arith() {
        return bin_op(usused -> term(), List.of(TT_PLUS, TT_MINUS), null);
    }

    /**
     * Checks for a binary operation between two or more nodes based on parameters.
     * @param leftFunc - the method to call on the left side of the binary operator.
     * @param ops - a List of operator tokens to check for between Nodes
     * @param rightFunc - the method to call on the right side of the binary operator.
     * @return a ParseResult holding either a Node or Error - The Node is of type BinaryOperationNode, or it passes the node on the left.
     */
    public ParseResult bin_op(Function<Void, ParseResult> leftFunc, List<TokenEnum> ops, Function<Void, ParseResult> rightFunc) {
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
