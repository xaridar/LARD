package xaridar.lscript.lexing;

import xaridar.lscript.Constants;
import xaridar.lscript.TokenEnum;
import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static xaridar.lscript.TokenEnum.*;


/**
 * This class turns a String of text into a list of lexed Tokens, containing all necessary information of Parsing the information.
 */
public class Lexer {
    private Character currentChar;
    private final Position pos;
    private final String text;

    /**
     * @param fn - The name of the file being lexed.
     * @param text - The text to be lexed.
     */
    public Lexer(String fn, String text) {
        this.text = text;
        this.pos = new Position(-1, 0, -1, fn, text);
        this.currentChar = null;
        advance();
    }

    /**
     * Advances the current position and sets the current character of the Lexer accordingly.
     */
    public void advance() {
        pos.advance(currentChar);
        currentChar = pos.getIdx() < text.length() ? text.charAt(pos.getIdx()) : null;
    }

    /**
     * Creates a Tuple containing a list of Tokens and an Error as a result of lexed text.
     * @return a Tuple containing a list of Tokens or an Error.
     */
    public Tuple<List<Token>, Error> makeTokens() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != null) {
            if (" \t\n\r".contains(currentChar.toString())) {
                advance();
            } else if ((currentChar.toString().matches("[0-9]"))) {
                Tuple<Token, Error> num = makeNumber();
                if(num.getRight() != null) return Tuple.of(null, num.getRight());
                tokens.add(num.getLeft());
            } else if (currentChar.toString().matches("[a-zA-Z_]")) {
                tokens.add(makeId());
            } else if (currentChar == '/') {
                advance();
                if (currentChar != null && currentChar == '/') {
                    makeComment("\n", false);
                } else if (currentChar != null && currentChar == '*') {
                    Error err = makeComment("*/", true);
                    if (err != null) return Tuple.of(new ArrayList<>(), err);
                } else {
                    Tuple<Token, Error> tup = parseEq(Constants.getInstance().EQUAL_MODS.get('/').get("with"), Constants.getInstance().EQUAL_MODS.get('/').get("without"), currentChar.toString(), false);
                    if (tup.getRight() != null)
                        return Tuple.of(new ArrayList<>(), tup.getRight());
                    tokens.add(tup.getLeft());
                }
            } else if (Constants.getInstance().EQUAL_MODS.containsKey(currentChar)) {
                Tuple<Token, Error> tup = parseEq(Constants.getInstance().EQUAL_MODS.get(currentChar).get("with"), Constants.getInstance().EQUAL_MODS.get(currentChar).get("without"), currentChar.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == '(') {
                tokens.add(new Token(TT_LEFT_PAREN, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == ')') {
                tokens.add(new Token(TT_RIGHT_PAREN, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '[') {
                tokens.add(new Token(TT_LEFT_BRACKET, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == ']') {
                tokens.add(new Token(TT_RIGHT_BRACKET, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '{') {
                tokens.add(new Token(TT_LEFT_BRACE, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '}') {
                tokens.add(new Token(TT_RIGHT_BRACE, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == ';') {
                tokens.add(new Token(TT_SEMICOLON, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '?') {
                tokens.add(new Token(TT_QUESTION, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == ':') {
                tokens.add(new Token(TT_COLON, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '=') {
                Tuple<Token, Error> tup = parseEq(TT_BOOLEQ, TT_EQ, currentChar.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == '\'') {
                Tuple<Token, Error> tup = makeString(currentChar);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == '"') {
                Tuple<Token, Error> tup = makeString(currentChar);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == ',') {
                tokens.add(new Token(TT_COMMA, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '.') {
                tokens.add(new Token(TT_DOT, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '|') {
                tokens.add(new Token(TT_PIPE, null, pos.copy(), null, "|"));
                advance();
            } else if (currentChar == '&') {
                tokens.add(new Token(TT_AND, null, pos.copy(), null, currentChar.toString()));
                advance();
            } else if (currentChar == '<') {
                Tuple<Token, Error> tup = parseEq(TT_LEQ, TT_LT, currentChar.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == '>') {
                Tuple<Token, Error> tup = parseEq(TT_GEQ, TT_GT, currentChar.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else if (currentChar == '!') {
                Tuple<Token, Error> tup = parseEq(TT_NEQ, TT_BANG, currentChar.toString(), true);
                if (tup.getRight() != null)
                    return Tuple.of(new ArrayList<>(), tup.getRight());
                tokens.add(tup.getLeft());
            } else {
                Position posStart = pos.copy();
                Character chara = currentChar;
                advance();
                return Tuple.of(new ArrayList<>(), new Error.IllegalCharError(posStart, pos, "'" + chara +"'"));
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

        while (currentChar != null && (currentChar != endChar || escapeChar)) {
            if (escapeChar) {
                str.append(escChars.stream().filter(tup -> tup.getLeft().equals(currentChar)).map(Tuple::getRight).findFirst().orElse(currentChar));
                escapeChar = false;
            }
            else {
                if (currentChar == '\\') {
                    escapeChar = true;
                } else {
                    str.append(currentChar);
                    escapeChar = false;
                }
            }
            advance();
        }
        if (currentChar != endChar) {
            return Tuple.of(null, new Error.ExpectedCharError(pos, pos.copy().advance(null), "Expected " + endChar));
        }
        advance();
        return Tuple.of(new Token(TT_STR, str.toString(), posStart, pos.copy(), null), null);
    }

    /**
     * Lexes a Token differently depending on whether the next character is an '=' symbol.
     * @param eqCase - A string representing the type to pass to the created Token if an '=' is found.
     * @param elseCase - A string representing the type to pass to the created Token if an '=' is not found. Can be null.
     * @param character - The character that the method is called on.
     * @param advance - A boolean representing whether the method should advance before doing any lexing.
     * @return a Tuple containing either a lexed token of either provided type, or an Error if the elseCase parameter is null.
     */
    public Tuple<Token, Error> parseEq(TokenEnum eqCase, TokenEnum elseCase, String character, boolean advance) {
        Position posStart = pos.copy();
        if (advance) advance();
        if (currentChar != null && currentChar == '=') {
            Position posEnd = pos.copy();
            advance();
            return Tuple.of(new Token(eqCase, null, posStart, posEnd, character), null);
        }
        else {
            if (elseCase != null)
                return Tuple.of(new Token(elseCase, null, posStart, null, character), null);
            else
                return Tuple.of(null, new Error.ExpectedCharError(posStart, pos, "'='"));
        }
    }

    /**
     * Lexes a comment, ending with the specified String - looks for a '\n' character for single line comments or an ending String for a multiline comment.
     * @param endStr - A substring or character to end the comment with.
     * @param endRequired - A boolean representing whether the end character must occur before the end of a file to avoid an error.
     * @return an Error from lexing the comment. Can be null.
     */
    public Error makeComment(String endStr, boolean endRequired) {
        StringBuilder str = new StringBuilder();
        advance();

        while (currentChar != null && !str.toString().endsWith(endStr)) {
            str.append(currentChar);
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
    public Tuple<Token, Error> makeNumber() {
        StringBuilder numStr = new StringBuilder();
        int periodCount = 0;
        Position posStart = pos.copy();
        String regexContained = ".0-9";

        while (currentChar != null && currentChar.toString().matches("[" + regexContained + "]")) {
            if (currentChar == '.') {
                if (periodCount == 1) {
                    break;
                }
                periodCount ++;
                numStr.append('.');
            }
            else {
                numStr.append(currentChar);
            }
            advance();
            if (currentChar == 'x' && (numStr.toString().matches("^0*$") && numStr.length() == 1)) {
                Tuple<Token, Error> hexToken = makeHex(posStart);
                if (hexToken.getRight() != null) return Tuple.of(null, hexToken.getRight());
                else return Tuple.of(hexToken.getLeft(), null);
            }
        }

        if (Float.parseFloat(numStr.toString()) > 2147483647) {
            return Tuple.of(null, new Error.InvalidSyntaxError(posStart, pos, "Integer overflow."));
        }
        if (periodCount == 0)
            return Tuple.of(new Token(TT_INT, Integer.valueOf(numStr.toString()), posStart, pos.copy(), null), null);
        else
            return Tuple.of(new Token(TT_FLOAT, Float.valueOf(numStr.toString()), posStart, pos.copy(), null), null);
    }

    /**
     * Lexes a number Token after a '0x' or '00x' is read.
     * @param posStart - The start Position of the hex Token.
     * @return a new Token, with type Int, or an Error.
     */
    private Tuple<Token, Error> makeHex(Position posStart) {
        advance();
        StringBuilder hex = new StringBuilder();
        if (!currentChar.toString().matches("[0-9a-fA-F]")) {
            return Tuple.of(null, new Error.InvalidSyntaxError(posStart, pos, "Expected hexadecimal character; got '" + currentChar + "'"));
        }
        while (currentChar != null && currentChar.toString().matches("[0-9a-fA-F]")) {
            hex.append(currentChar);
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
        return Tuple.of(new Token(TT_INT, converted, posStart, pos.copy(), null), null);
    }

    /**
     * Lexes a Token with either a Keyword or Identifier type from letters a-z (case insensitive), 0-9, or _
     * @return a new Token, with type Keyword or Identifier.
     */
    public Token makeId() {
        StringBuilder identifier = new StringBuilder();
        Position posStart = pos.copy();

        while (currentChar != null && currentChar.toString().matches("[0-9a-zA-Z_]")) {
            identifier.append(currentChar);
            advance();
        }

        if (Constants.getInstance().DESIGNATED_KEYWORDS.contains(identifier.toString()))
            return new Token(TT_KW, identifier.toString(), posStart, pos.copy(), null);
        else
            return new Token(TT_IDENTIFIER, identifier.toString(), posStart, pos.copy(), null);
    }

}
