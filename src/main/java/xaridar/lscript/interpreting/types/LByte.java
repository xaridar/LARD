package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;

public class LByte extends BasicType {

    private final byte val;

    public LByte(byte val) {
        super("byte");
        this.val = val;
    }

    @SuppressWarnings("unused")
    public static RunTimeResult from(Value val) {
        RunTimeResult res = new RunTimeResult();
        if (val instanceof LNumber) {
            if (((LNumber) val).getValue().byteValue() < Byte.MAX_VALUE && ((LNumber) val).getValue().byteValue() < Byte.MAX_VALUE) {
                return res.success(new LByte(((LNumber) val).getValue().byteValue()).setContext(val.getContext()).setPos(val.getPosStart(), val.getPosEnd()));
            }
            return res.failure(new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "Cannot convert int to byte.", val.getContext()));
        }
        return res.failure(new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "Cannot convert '" + val.getType() + "' to byte.", val.getContext()));
    }

    @Override
    public String toString() {
        return Byte.toString(val);
    }

    @Override
    public BasicType copy() {
        return new LByte(val).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Byte getValue() {
        return Byte.valueOf(val);
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LByte)) return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), other.getPosEnd());
        return (LBoolean) new LBoolean(val == ((LByte) other).getValue()).setContext(getContext()).setPos(getPosStart(), other.getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof LByte)) return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), other.getPosEnd());
        return (LBoolean) new LBoolean(val != ((LByte) other).getValue()).setContext(getContext()).setPos(getPosStart(), other.getPosEnd());
    }

    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        if (other instanceof LInt || other instanceof LByte) {
            LByte num;
            if (other instanceof LByte)
                num = new LByte((byte) (val + ((LByte) other).getValue()));
            else
                num = new LByte((byte) (val + ((LInt) other).getValue()));
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;
    }
}
