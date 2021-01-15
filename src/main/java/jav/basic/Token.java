package jav.basic;

public class Token {
    String type;
    Object value;
    String opChar;
    Position posStart = null;
    Position posEnd = null;

    public Token(String type, Object value, Position posStart, Position posEnd, String opChar) {
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

    public String getOpChar() {
        return opChar;
    }

    @Override
    public String toString() {
        if (value != null) {
            return type + ":" + value;
        }
        return type;
    }

    public boolean matches(String type, Object value) {
        return this.type.equals(type) && this.value.equals(value);
    }

    public Position getPosStart() {
        return posStart;
    }

    public Position getPosEnd() {
        return posEnd;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
