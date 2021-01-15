package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

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
        public Tuple<BasicType, Error> equalTo(Value other) {
            return Tuple.of(new jav.basic.types.Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }

        @Override
        public Tuple<BasicType, Error> notEqualTo(Value other) {
            return Tuple.of(new jav.basic.types.Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
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
    public Tuple<BasicType, Error> equalTo(Value other) {
        if (!(other instanceof NullType)) {
            return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return Tuple.of(new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> notEqualTo(Value other) {
        if (!(other instanceof NullType)) {
            return Tuple.of(new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }
}
