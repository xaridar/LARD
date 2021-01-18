package lscript.lexing;

import lscript.TokenEnum;

/**
 * A piece of data created by the Lexer that stores information about one small piece of input for convenience in parsing.
 */
public class Token {
    TokenEnum type;
    Object value;
    String opChar;
    Position posStart = null;
    Position posEnd = null;

    /**
     * @param type - The type of the Token, as a String. All possible Token types are stored as constants in lscript.Constants.
     * @param value - The value of the token, if it has one. Can be null.
     * @param posStart - The start Position of the Token in the original text.
     * @param posEnd - The end Position of the Token in the original text.
     * @param opChar - The operation character represented by the Token, if there is one. Can be null.
     */
    public Token(TokenEnum type, Object value, Position posStart, Position posEnd, String opChar) {
        this.type = type;
        this.value = value;
        this.opChar = opChar;
        if (posStart != null) {
            this.posStart = posStart.copy();
            this.posEnd = posStart.copy();
            this.posEnd.advance(null);
        }
        if (posEnd != null) {
            this.posEnd = posEnd;
        }
    }

    /**
     * @return The operation character represented by the Token, if there is one. Can be null.
     */
    public String getOpChar() {
        return opChar;
    }

    /**
     * @return A String representation of the Token, for ease in debugging.
     */
    @Override
    public String toString() {
        if (value != null) {
            return type.name() + ":" + value;
        }
        return type.name();
    }

    /**
     * Tests whether the type and value of this Token match the parameter.
     * @param type - A Token type.
     * @param value - A token value.
     * @return true if both parameters match this Token's fields.
     */
    public boolean matches(TokenEnum type, Object value) {
        return this.type.equals(type) && this.value.equals(value);
    }

    /**
     * @return The start Position of the Token in the original text.
     */
    public Position getPosStart() {
        return posStart;
    }

    /**
     * @return The end Position of the Token in the original text.
     */
    public Position getPosEnd() {
        return posEnd;
    }

    /**
     * @return The type of the Token, as a String. All possible Token types are stored as constants in lscript.Constants.
     */
    public TokenEnum getType() {
        return type;
    }

    /**
     * @return The value of the token, if it has one. Can be null.
     */
    public Object getValue() {
        return value;
    }
}
