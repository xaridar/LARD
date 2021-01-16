package jav.basic;

import jav.Constants;
import jav.Tuple;
import jav.basic.nodes.*;
import jav.basic.results.ParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static jav.Constants.*;

public class Parser {

    private Token currentToken;
    private Token nextToken;
    private List<Token> tokens;
    private int tokenIndex;

    public Parser(List<Token> tokens) {
        this.currentToken = null;
        this.nextToken = tokens.get(0);
        this.tokens = tokens;
        this.tokenIndex = -1;
        advance();
    }

    public Token reverse(int num) {
        tokenIndex -= num;
        updateTokens();
        return currentToken;
    }

    public void updateTokens() {
        if (tokenIndex < tokens.size()) {
            currentToken = tokens.get(tokenIndex);
            if (tokenIndex < tokens.size() - 1)
                nextToken = tokens.get(tokenIndex + 1);
            else
                nextToken = null;
        }
    }

    public ParseResult parse() {
        ParseResult res = statements();
        if (!res.hasError() && !currentToken.getType().equals(TT_EOF))
            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(),
                    "Expected ';', '+', '-', '*', '/', '^', '%', '==', '!', '!=', '<', '>', '<=', '>=', '+=', '-=', '*=', '/=', '&', or '|'"));
        return res;
    }

    public Token advance() {
        tokenIndex++;
        updateTokens();
        return currentToken;
    }

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
        } else if (tok.type.equals(TT_IDENTIFIER)) {
            res.registerAdvancement();
            advance();
            return res.success(new VarAccessNode(tok));
        } else if (tok.type.equals(TT_LEFT_PAREN)) {
            res.registerAdvancement();
            advance();
            Node expr = res.register(expression());
            if (res.hasError()) return res;
            if (currentToken.type.equals(TT_RIGHT_PAREN)) {
                res.registerAdvancement();
                advance();
                return res.success(expr);
            }
            else return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));
        } else if (tok.matches(TT_KW, "if")) {
            Node ifExpr = res.register(ifExpr());
            if (res.hasError()) return res;
            return res.success(ifExpr);
//        } else if (tok.matches(TT_KW, "switch")) {
//            Node switchCase = res.register(switchCase());
//            if (res.hasError()) return res;
//            return res.success(switchCase);
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

//    private ParseResult switchCase() {
//        ParseResult res = new ParseResult();
//        List<Node> cases = new ArrayList<>();
//        if (!currentToken.matches(TT_KW, "switch"))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'switch'"));
//        res.registerAdvancement();
//        advance();
//        if (!currentToken.getType().equals(TT_LEFT_PAREN))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '('"));
//        res.registerAdvancement();
//        advance();
//        Node var = res.register(expression());
//        if (res.hasError()) return res;
//        Token eqToken = new Token(TT_BOOLEQ, null, currentToken.getPosStart(), null, null);
//
//        if (!currentToken.getType().equals(TT_RIGHT_PAREN))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ')'"));
//        res.registerAdvancement();
//        advance();
//
//        if (!currentToken.getType().equals(TT_LEFT_BRACE))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '{'"));
//        res.registerAdvancement();
//        advance();
//
//        if (!currentToken.matches(TT_KW, "case"))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected 'case'"));
//        res.registerAdvancement();
//        advance();
//        Node val = res.register(expression());
//        if (res.hasError()) return res;
//
//        if (!currentToken.getType().equals(TT_COLON))
//            return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ':'"));
//        res.registerAdvancement();
//        advance();
//        cases.add(new BinaryOperationNode(var, eqToken, val));
//
//    }

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
            if (!Constants.getInstance().TYPES.containsKey((String) currentToken.getValue()))
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

                if (!Constants.getInstance().TYPES.containsKey((String) currentToken.getValue()))
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
            if (!getInstance().TYPES.containsKey((String) currentToken.getValue()))
                return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
            returnTypes.add((String) currentToken.getValue());
            while (currentToken.getType().equals(TT_COMMA)) {
                res.registerAdvancement();
                advance();

                if (!currentToken.getType().equals(TT_KW))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                if (!getInstance().TYPES.containsKey((String) currentToken.getValue()))
                    return res.failure( new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected type"));
                returnTypes.add(currentToken.getType());
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

    private ParseResult ifExpr() {
        ParseResult res = new ParseResult();
        Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> allCases = ifExprCases("if");
        if (allCases.getRight() != null) return res.failure(allCases.getRight());

        return res.success(new ConditionalNode(allCases.getLeft().getLeft(), allCases.getLeft().getRight(), true));
    }

    private Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprCases(String caseKW) {
        ParseResult res = new ParseResult();
        List<Tuple<Tuple<Node, Node>, Boolean>> cases = new ArrayList<>();
        Tuple<Node, Boolean> else_case = null;

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

    public Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprBorC() {
        ParseResult res = new ParseResult();
        List<Tuple<Tuple<Node, Node>, Boolean>> cases = new ArrayList<>();
        Tuple<Node, Boolean> elseCase = null;

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

    public Tuple<Tuple<List<Tuple<Tuple<Node, Node>, Boolean>>, Tuple<Node, Boolean>>, Error> ifExprB() {
        return ifExprCases("elif");
    }

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

    public ParseResult power() {
        return bin_op(unused -> call(), List.of(TT_POW), (unused -> factor()));
    }

    public ParseResult call() {
        ParseResult res = new ParseResult();
        Node atom = res.register(atom());
        if (res.hasError()) return res;

        if (currentToken.getType().equals(TT_LEFT_PAREN)) {
            res.registerAdvancement();
            advance();
            List<Node> arg_nodes = new ArrayList<>();

            if (currentToken.getType().equals(TT_RIGHT_PAREN)) {
                res.registerAdvancement();
                advance();
            } else {
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

                res.registerAdvancement();
                advance();
            }
            if (atom instanceof VarAccessNode)
                return res.success(new CallNode((VarAccessNode) atom, arg_nodes));
            return res.failure(new Error.InvalidSyntaxError(atom.getPosStart(), atom.getPosEnd(), "Expected identifier"));
        }
        return res.success(atom);
    }

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

    public ParseResult term() {
        return bin_op(unused -> factor(), List.of(TT_MUL, TT_DIV, TT_MOD), null);
    }

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
            } else {
                reverse(res.getToReverseCount());

                if (!currentToken.getType().equals(TT_SEMICOLON) && statements.size() > 1 && statements.get(statements.size() - 1).requiresSemicolon()) {
                    return res.failure(new Error.ExpectedCharError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ';'"));
                } else if (statements.size() != 1) {

                    res.registerAdvancement();
                    advance();
                }
                break;
            }
        }

        return res.success(new ListNode(statements, posStart, currentToken.getPosEnd()));
    }

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
        if (res.hasError()) return res.failure(new Error.InvalidSyntaxError(posStart, currentToken.getPosEnd(), "Expected type, 'return', 'continue', 'break', 'if', 'for', 'while', 'func', value, identifier, '+', '-', '(', '[', '{', or '!'"));
        return res.success(expression);
    }

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

            Node index = res.register(expr());
            if (res.hasError()) return res;

            if (!currentToken.getType().equals(TT_RIGHT_BRACKET))
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected ']'"));

            res.registerAdvancement();
            advance();

            if (currentToken.getType().equals(TT_EQ)) {
                res.registerAdvancement();
                advance();

                Node toSet = res.register(expr());
                if (res.hasError()) return res;
                return res.success(new SetIndexNode(left, index, toSet));
            }

            return res.success(new IndexNode(left, index));
        }

        return res.success(left);
    }

    public ParseResult expr() {
        ParseResult res = new ParseResult();
        if (currentToken.getType().equals(TT_KW)) {
            if (Constants.getInstance().TYPES.containsKey((String) currentToken.getValue())) {
                String type = (String) currentToken.getValue();
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
                }
                return res.failure(new Error.InvalidSyntaxError(currentToken.getPosStart(), currentToken.getPosEnd(), "Expected '='"));
            }
        } else if (currentToken.getType().equals(TT_IDENTIFIER)) {
            Token var_name = currentToken;
            Map<String, String> mods = Constants.getInstance().EQUAL_MODS.values().stream().collect(Collectors.toMap(m -> m.get("with"), m -> m.get("without")));
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
                    String to_token = mods.get(nextToken.getType());
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

    public ParseResult arith() {
        return bin_op(usused -> term(), List.of(TT_PLUS, TT_MINUS), null);
    }

    public ParseResult bin_op(Function<Void, ParseResult> leftFunc, List<String> ops, Function<Void, ParseResult> rightFunc) {
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
