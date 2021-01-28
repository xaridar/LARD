package xaridar.lscript.interpreting.types;

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;

public abstract class LNumber extends BasicType {

    java.lang.Number value;

    public LNumber(String type, java.lang.Number value) {
        super(type);
        this.value = value;
    }

    @Override
    public java.lang.Number getValue() {
        return value;
    }

    // operations

    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        if (other instanceof LNumber || other instanceof LByte) {
            LNumber num;
            if (this instanceof LInt && (other instanceof LInt || other instanceof LByte))
                num = new LInt(value.intValue() + ((LNumber) other).getValue().intValue());
            else
                num = new LFloat(value.floatValue() + ((LNumber) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;
    }

    @Override
    public Tuple<BasicType, Error> subtractedBy(BasicType other) {
        if (other instanceof LNumber) {
            LNumber num;
            if (this instanceof LInt && other instanceof LInt)
                num = new LInt(value.intValue() - ((LNumber) other).getValue().intValue());
            else
                num = new LFloat(value.floatValue() - ((LNumber) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> multipliedBy(BasicType other) {
        if (other instanceof LNumber) {
            LNumber num;
            if (this instanceof LInt && other instanceof LInt)
                num = new LInt(value.intValue() * ((LNumber) other).getValue().intValue());
            else
                num = new LFloat(value.floatValue() * ((LNumber) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> dividedBy(BasicType other) {
        if (other instanceof LNumber) {
            if (((LNumber) other).getValue().floatValue() == 0)
                return Tuple.of(null, new Error.RunTimeError(other.getPosStart(), other.getPosEnd(), "Division by 0", getContext()));
            LNumber num;
            java.lang.Number otherValue = ((LNumber) other).getValue();
            if (this instanceof LInt && other instanceof LInt && value.floatValue() / otherValue.floatValue() == value.intValue() / otherValue.intValue()) {
                num = new LInt(value.intValue() / otherValue.intValue());
            } else
                num = new LFloat(value.floatValue() / otherValue.floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> toThePowerOf(BasicType other) {
        if (other instanceof LNumber) {
            LNumber num;
            java.lang.Number endResult = Double.valueOf(Math.pow(value.floatValue(), ((LNumber) other).getValue().floatValue()));
            if (this instanceof LInt && endResult.floatValue() == endResult.intValue())
                num = new LInt(endResult.intValue());
            else
                num = new LFloat(endResult.floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> modulo(BasicType other) {
        if (other instanceof LNumber) {
            LNumber num;
            if (this instanceof LInt && other instanceof LInt)
                num = new LInt(value.intValue() % ((LInt) other).getValue());
            else
                num = new LFloat(value.floatValue() % ((LNumber) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> lessThan(BasicType other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() < ((LNumber) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> greaterThan(BasicType other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() > ((LNumber) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> lessThanOrEqualTo(BasicType other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() <= ((LNumber) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> greaterThanOrEqualTo(BasicType other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() >= ((LNumber) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public LBoolean equalTo(Value other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() == ((LNumber) other).getValue().floatValue());
            return (LBoolean) (bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()));
        }
        return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());

    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (other instanceof LNumber) {
            LBoolean bool = new LBoolean(value.floatValue() != ((LNumber) other).getValue().floatValue());
            return (LBoolean) bool.setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());

    }
}
