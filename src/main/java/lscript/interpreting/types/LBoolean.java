package lscript.interpreting.types;

import lscript.Tuple;
import lscript.errors.Error;

public class LBoolean extends BasicType {

    public static LBoolean True = new LBoolean(true);
    public static LBoolean False = new LBoolean(false);
    private final boolean value;

    public LBoolean(boolean value) {
        super("bool");
        this.value = value;
    }

    @Override
    public LBoolean copy() {
        LBoolean b = new LBoolean(value);
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
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LBoolean)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(value == ((LBoolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof LBoolean)) {
            return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(value != ((LBoolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Tuple<BasicType, Error> and(BasicType other) {
        if (!(other instanceof LBoolean)) {
            return null;
        }
        return Tuple.of(new LBoolean(value && ((LBoolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> or(BasicType other) {
        if (!(other instanceof LBoolean)) {
            return null;
        }
        return Tuple.of(new LBoolean(value || ((LBoolean) other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> reversed() {
        return Tuple.of(new LBoolean(!value).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public boolean isTrue() {
        return value;
    }
}
