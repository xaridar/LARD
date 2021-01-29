package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */


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
        public LBoolean equalTo(Value other) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }

        @Override
        public LBoolean notEqualTo(Value other) {
            return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
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
    public LBoolean equalTo(Value other) {
        if (!(other instanceof NullType)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof NullType)) {
            return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
