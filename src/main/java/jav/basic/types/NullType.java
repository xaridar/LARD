package jav.basic.types;

import jav.Tuple;

public class NullType extends BasicType {

    public static NullType Null = new NullType();
    public static Value Void = new BasicType("void") {
        @Override
        public String toString() {
            return "<void>";
        }

        @Override
        public Value copy() {
            return this;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Boolean equalTo(Value other) {
            return (Boolean) new jav.basic.types.Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }

        @Override
        public Boolean notEqualTo(Value other) {
            return (Boolean) new jav.basic.types.Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
    };

    public NullType() {
        super("nullType");
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Value copy() {
        return new NullType();
    }

    @Override
    public Object getValue() {
        return NullType.Null;
    }

    @Override
    public Boolean equalTo(Value other) {
        if (!(other instanceof NullType)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        if (!(other instanceof NullType)) {
            return (Boolean) new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
