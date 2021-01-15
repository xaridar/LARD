package jav;

import jav.basic.Context;
import jav.basic.Error;
import jav.basic.Position;
import jav.basic.Token;

import java.util.ArrayList;
import java.util.List;

import static jav.Constants.*;

public class Lexer {
    private Character current_char;
    private String fn;
    private Position pos;
    private String text;
    private Context context;

    public Lexer (String fn, String text, Context context) {
        this.fn = fn;
        this.text = text;
        this.pos = new Position(-1, 0, -1, fn, text);
        this.current_char = null;
        this.context = context;
        advance();
    }

    public void advance() {
        pos.advance(current_char);
        current_char = pos.getIdx() < text.length() ? text.charAt(pos.getIdx()) : null;
    }

    public Tuple<List<Token>, Error> make_tokens() {
        List<Token> tokens = new ArrayList<>();
        while (current_char != null) {
            if (" \t\n\r".contains(current_char.toString())) {
                advance();
            } else if ((current_char.toString().matches("[0-9]"))) {
                tokens.add(make_number());
            } else if (current_char.toString().matches("[a-zA-Z_]")) {
                tokens.add(make_id());
            } else if (Constants.getInstance().EQUAL_MODS.containsKey(current_char)) {
                Tuple<Token, Error> tup = parse_eq(Constants.getInstance().EQUAL_MODS.get(current_char).get("with"), Constants.getInstance().EQUAL_MODS.get(current_char).get("without"), current_char.toString());
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '(') {
                tokens.add(new Token(Constants.TT_LEFT_PAREN, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == ')') {
                tokens.add(new Token(Constants.TT_RIGHT_PAREN, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '[') {
                tokens.add(new Token(Constants.TT_LEFT_BRACKET, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == ']') {
                tokens.add(new Token(Constants.TT_RIGHT_BRACKET, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '{') {
                tokens.add(new Token(Constants.TT_LEFT_BRACE, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '}') {
                tokens.add(new Token(Constants.TT_RIGHT_BRACE, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == ';') {
                tokens.add(new Token(Constants.TT_SEMICOLON, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '?') {
                tokens.add(new Token(Constants.TT_QUESTION, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == ':') {
                tokens.add(new Token(Constants.TT_COLON, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '=') {
                Tuple<Token, Error> tup = parse_eq(Constants.TT_BOOLEQ, Constants.TT_EQ, current_char.toString());
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '\'') {
                Tuple<Token, Error> tup = makeString(current_char);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '"') {
                Tuple<Token, Error> tup = makeString(current_char);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == ',') {
                tokens.add(new Token(Constants.TT_COMMA, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '|') {
                Position pos_start = pos.copy();
                advance();
                if (current_char != null && current_char == '=') {
                    Position pos_end = pos.copy();
                    advance();
                    tokens.add(new Token(TT_DOUBLE_PIPE, null, pos_start, pos_end, "||"));
                }
                else
                    tokens.add(new Token(TT_PIPE, null, pos_start, null, "|"));
            } else if (current_char == '&') {
                tokens.add(new Token(Constants.TT_AND, null, pos, null, current_char.toString()));
                advance();
            } else if (current_char == '<') {
                Tuple<Token, Error> tup = parse_eq(Constants.TT_LEQ, Constants.TT_LT, current_char.toString());
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '>') {
                Tuple<Token, Error> tup = parse_eq(Constants.TT_GEQ, Constants.TT_GT, current_char.toString());
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '!') {
                Tuple<Token, Error> tup = parse_eq(Constants.TT_NEQ, Constants.TT_BANG, current_char.toString());
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else {
                Position pos_start = pos.copy();
                Character chara = current_char;
                advance();
                return Tuple.of(new ArrayList<>(), new Error.IllegalCharError(pos_start, pos, "'" + chara +"'"));
            }
        }
        tokens.add(new Token(Constants.TT_EOF, null, pos, null, null));
        return Tuple.of(tokens, null);
    }

    public Tuple<Token, Error> makeString(Character endChar) {
        StringBuilder str = new StringBuilder();
        Position posStart = pos.copy();
        boolean escapeChar = false;
        advance();

        List<Tuple<Character, Character>> escChars = List.of(
                Tuple.of('n', '\n'),
                Tuple.of('t', '\t')
        );

        while (current_char != null && (current_char != endChar || escapeChar)) {
            if (escapeChar) {
                str.append(escChars.stream().filter(tup -> tup.getLeft().equals(current_char)).map(Tuple::getRight).findFirst().orElse(current_char));
                escapeChar = false;
            }
            else {
                if (current_char == '\\') {
                    escapeChar = true;
                } else {
                    str.append(current_char);
                    escapeChar = false;
                }
            }
            advance();
        }
        if (current_char != endChar) {
            return Tuple.of(null, new Error.ExpectedCharError(pos, pos.copy().advance(null), "Expected " + endChar));
        }
        advance();
        return Tuple.of(new Token(TT_STR, str.toString(), posStart, pos, null), null);
    }

    public Tuple<Token, Error> parse_eq(String eq_case, String else_case, String character) {
        Position pos_start = pos.copy();
        advance();
        if (current_char != null && current_char == '=') {
            Position pos_end = pos.copy();
            advance();
            return Tuple.of(new Token(eq_case, null, pos_start, pos_end, character), null);
        }
        else {
            if (else_case != null)
                return Tuple.of(new Token(else_case, null, pos_start, null, character), null);
            else
                return Tuple.of(null, new Error.ExpectedCharError(pos_start, pos, "'='"));
        }
    }

    public Token make_number() {
        StringBuilder num_str = new StringBuilder();
        int period_count = 0;
        Position pos_start = pos.copy();

        while (current_char != null && current_char.toString().matches("[.0-9]")) {
            if (current_char == '.') {
                if (period_count == 1) {
                    break;
                }
                period_count ++;
                num_str.append('.');
            }
            else {
                num_str.append(current_char);
            }
            advance();
        }

        if (period_count == 0)
            return new Token(Constants.TT_INT, Integer.parseInt(num_str.toString()), pos_start, pos, null);
        else
            return new Token(Constants.TT_FLOAT, Float.parseFloat(num_str.toString()), pos_start, pos, null);
    }

    public Token make_id() {
        StringBuilder identifier = new StringBuilder();
        Position pos_start = pos.copy();

        while (current_char != null && current_char.toString().matches("[0-9a-zA-Z_]")) {
            identifier.append(current_char);
            advance();
        }

        if (Constants.getInstance().DESIGNATED_KEYWORDS.contains(identifier.toString()))
            return new Token(Constants.TT_KW, identifier.toString(), pos_start, pos, null);
        else
            return new Token(Constants.TT_IDENTIFIER, identifier.toString(), pos_start, pos, null);
    }

}
