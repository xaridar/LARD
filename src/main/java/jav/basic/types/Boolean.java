package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

public class Boolean extends BasicType {

    public static Boolean True = new Boolean(true);
    public static Boolean False = new Boolean(false);
    private boolean value;

    public Boolean(boolean value) {
        super("bool");
        this.value = value;
    }

    @Override
    public Boolean copy() {
        Boolean b = new Boolean(value);
        b.setContext(getContext());
        b.setPos(getPosStart(), getPosEnd());
        return b;
    }

    @Override
    public java.lang.Boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return (value ? "true" : "false");
    }

    // ops


    @Override
    public Boolean equalTo(Value other) {
        if (!(other instanceof Boolean)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(value == ((Boolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        if (!(other instanceof Boolean)) {
            return (Boolean) new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(value != ((Boolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Tuple<BasicType, Error> and(BasicType other) {
        if (!(other instanceof Boolean)) {
            return null;
        }
        return Tuple.of(new Boolean(value && ((Boolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> or(BasicType other) {
        if (!(other instanceof Boolean)) {
            return null;
        }
        return Tuple.of(new Boolean(value || ((Boolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> reversed() {
        return Tuple.of(new Boolean(!value).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public boolean isTrue() {
        return value;
    }
}
