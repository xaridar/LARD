package lscript.lexing;

import lscript.Constants;
import lscript.TokenEnum;
import lscript.Tuple;
import lscript.errors.Error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lscript.TokenEnum.*;


/**
 * This class turns a String of text into a list of lexed Tokens, containing all necessary information of Parsing the information.
 */
public class Lexer {
    private Character current_char;
    private final Position pos;
    private final String text;

    /**
     * @param fn - The name of the file being lexed.
     * @param text - The text to be lexed.
     */
    public Lexer(String fn, String text) {
        this.text = text;
        this.pos = new Position(-1, 0, -1, fn, text);
        this.current_char = null;
        advance();
    }

    /**
     * Advances the current position and sets the current character of the Lexer accordingly.
     */
    public void advance() {
        pos.advance(current_char);
        current_char = pos.getIdx() < text.length() ? text.charAt(pos.getIdx()) : null;
    }

    /**
     * Creates a Tuple containing a list of Tokens and an Error as a result of lexed text.
     * @return a Tuple containing a list of Tokens or an Error.
     */
    public Tuple<List<Token>, Error> make_tokens() {
        List<Token> tokens = new ArrayList<>();
        while (current_char != null) {
            if (" \t\n\r".contains(current_char.toString())) {
                advance();
            } else if ((current_char.toString().matches("[0-9]"))) {
                Tuple<Token, Error> num = make_number();
                if(num.getRight() != null) return Tuple.of(null, num.getRight());
                tokens.add(num.getLeft());
            } else if (current_char.toString().matches("[a-zA-Z_]")) {
                tokens.add(make_id());
            } else if (current_char == '/') {
                advance();
                if (current_char != null && current_char == '/') {
                    makeComment("\n", TT_OL_COMMENT, false);
                } else if (current_char != null && current_char == '*') {
                    makeComment("*/", TT_ML_COMMENT, true);
                } else {
                    Tuple<Token, Error> tup = parse_eq(Constants.getInstance().EQUAL_MODS.get(Character.valueOf('/')).get("with"), Constants.getInstance().EQUAL_MODS.get(Character.valueOf('/')).get("without"), current_char.toString(), false);
                    if (tup.getRight() != null)
                        return Tuple.of(new ArrayList<>(), tup.getRight());
                    tokens.add(tup.getLeft());
                }
            } else if (Constants.getInstance().EQUAL_MODS.containsKey(current_char)) {
                Tuple<Token, Error> tup = parse_eq(Constants.getInstance().EQUAL_MODS.get(current_char).get("with"), Constants.getInstance().EQUAL_MODS.get(current_char).get("without"), current_char.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '(') {
                tokens.add(new Token(TT_LEFT_PAREN, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == ')') {
                tokens.add(new Token(TT_RIGHT_PAREN, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '[') {
                tokens.add(new Token(TT_LEFT_BRACKET, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == ']') {
                tokens.add(new Token(TT_RIGHT_BRACKET, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '{') {
                tokens.add(new Token(TT_LEFT_BRACE, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '}') {
                tokens.add(new Token(TT_RIGHT_BRACE, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == ';') {
                tokens.add(new Token(TT_SEMICOLON, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '?') {
                tokens.add(new Token(TT_QUESTION, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == ':') {
                tokens.add(new Token(TT_COLON, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '=') {
                Tuple<Token, Error> tup = parse_eq(TT_BOOLEQ, TT_EQ, current_char.toString(), true);
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
                tokens.add(new Token(TT_COMMA, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '.') {
                tokens.add(new Token(TT_DOT, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '|') {
                tokens.add(new Token(TT_PIPE, null, pos.copy(), null, "|"));
                advance();
            } else if (current_char == '&') {
                tokens.add(new Token(TT_AND, null, pos.copy(), null, current_char.toString()));
                advance();
            } else if (current_char == '<') {
                Tuple<Token, Error> tup = parse_eq(TT_LEQ, TT_LT, current_char.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '>') {
                Tuple<Token, Error> tup = parse_eq(TT_GEQ, TT_GT, current_char.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (current_char == '!') {
                Tuple<Token, Error> tup = parse_eq(TT_NEQ, TT_BANG, current_char.toString(), true);
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
        tokens.add(new Token(TT_EOF, null, pos.copy(), null, null));
        return Tuple.of(tokens, null);
    }

    /**
     * Creates a String token when a " or ' character is detected.
     * @param endChar - The character that starts the String, which will inform the methhod which character to end at; this is to ensure Strings ending in " cannot end in ', and vice verse.
     * @return a Tuple containing either a lexed String Token or an Error.
     */
    public Tuple<Token, Error> makeString(Character endChar) {
        StringBuilder str = new StringBuilder();
        Position posStart = pos.copy();
        boolean escapeChar = false;
        advance();

        List<Tuple<Character, Character>> escChars = Arrays.asList(
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
        return Tuple.of(new Token(TT_STR, str.toString(), posStart, pos.copy(), null), null);
    }

    /**
     * Lexes a Token differently depending on whether the next character is an '=' symbol.
     * @param eq_case - A string representing the type to pass to the created Token if an '=' is found.
     * @param else_case - A string representing the type to pass to the created Token if an '=' is not found. Can be null.
     * @param character - The character that the method is called on.
     * @param advance - A boolean representing whether the method should advance before doing any lexing.
     * @return a Tuple containing either a lexed token of either provided type, or an Error if the else_case parameter is null.
     */
    public Tuple<Token, Error> parse_eq(TokenEnum eq_case, TokenEnum else_case, String character, boolean advance) {
        Position pos_start = pos.copy();
        if (advance) advance();
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

    /**
     * Lexes a comment, ending with the specified String - looks for a '\n' character for single line comments or an ending String for a multiline comment.
     * @param endStr - A substring or character to end the comment with.
     * @param tokenType - A TokenEnum to give to the returned Token.
     * @param endRequired - A boolean representing whether the end character must occur before the end of a file to avoid an error.
     * @return an Error from lexing the comment. Can be null.
     */
    public Error makeComment(String endStr, TokenEnum tokenType, boolean endRequired) {
        StringBuilder str = new StringBuilder();
        advance();

        while (current_char != null && !str.toString().endsWith(endStr)) {
            str.append(current_char);
            advance();
        }
        if (!str.toString().endsWith(endStr) && endRequired) {
            return new Error.ExpectedCharError(pos, pos.copy().advance(null), "Expected " + endStr);
        }
        return null;
    }

    /**
     * Lexes a number Token of either type Int or Float.
     * @return a new Token, with type Int or Float. May also return an Error in the case of a failed hexadecimal lex.
     */
    public Tuple<Token, Error> make_number() {
        StringBuilder num_str = new StringBuilder();
        int period_count = 0;
        Position pos_start = pos.copy();
        String regexContained = ".0-9";

        while (current_char != null && current_char.toString().matches("[" + regexContained + "]")) {
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
            if (current_char == 'x' && (num_str.toString().matches("^0*$") && num_str.length() == 1)) {
                Tuple<Token, Error> hexToken = makeHex(pos_start);
                if (hexToken.getRight() != null) return Tuple.of(null, hexToken.getRight());
                else return Tuple.of(hexToken.getLeft(), null);
            }
        }

        if (Float.parseFloat(num_str.toString()) > 2147483647) {
            return Tuple.of(null, new Error.InvalidSyntaxError(pos_start, pos, "Integer overflow."));
        }
        if (period_count == 0)
            return Tuple.of(new Token(TT_INT, Integer.valueOf(num_str.toString()), pos_start, pos.copy(), null), null);
        else
            return Tuple.of(new Token(TT_FLOAT, Float.valueOf(num_str.toString()), pos_start, pos.copy(), null), null);
    }

    /**
     * Lexes a number Token after a '0x' or '00x' is read.
     * @param posStart - The start Position of the hex Token.
     * @return a new Token, with type Int, or an Error.
     */
    private Tuple<Token, Error> makeHex(Position posStart) {
        advance();
        StringBuilder hex = new StringBuilder();
        if (!current_char.toString().matches("[0-9a-fA-F]")) {
            return Tuple.of(null, new Error.InvalidSyntaxError(posStart, pos, "Expected hexadecimal character; got '" + current_char + "'"));
        }
        while (current_char != null && current_char.toString().matches("[0-9a-fA-F]")) {
            hex.append(current_char);
            advance();
        }
        if (hex.length() > 8) {
            return Tuple.of(null, new Error.InvalidSyntaxError(posStart, pos, "Integer overflow."));
        }
        int converted = 0;
        String finishedHex = hex.toString();
        finishedHex = String.format("%1$8s", finishedHex).replace(" ", "0");
        char[] charArray = finishedHex.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char chara = charArray[8- i - 1];
            int val;
            if (Character.toString(chara).matches("[a-fA-F]")) {
                val = "abcdef".indexOf(Character.toLowerCase(chara)) + 10;
            } else {
                val = Integer.parseInt(Character.toString(chara));
            }
            converted += val * Math.pow(16, i);
        }
        return Tuple.of(new Token(TT_INT, Integer.valueOf(converted), posStart, pos.copy(), null), null);
    }

    /**
     * Lexes a Token with either a Keyword or Identifier type from letters a-z (case insensitive), 0-9, or _
     * @return a new Token, with type Keyword or Identifier.
     */
    public Token make_id() {
        StringBuilder identifier = new StringBuilder();
        Position pos_start = pos.copy();

        while (current_char != null && current_char.toString().matches("[0-9a-zA-Z_]")) {
            identifier.append(current_char);
            advance();
        }

        if (Constants.getInstance().DESIGNATED_KEYWORDS.contains(identifier.toString()))
            return new Token(TT_KW, identifier.toString(), pos_start, pos.copy(), null);
        else
            return new Token(TT_IDENTIFIER, identifier.toString(), pos_start, pos.copy(), null);
    }

}
