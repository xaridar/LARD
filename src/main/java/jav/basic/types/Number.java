package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

public abstract class Number extends BasicType {

    java.lang.Number value;

    public Number(String type, java.lang.Number value) {
        super(type);
        this.value = value;
    }

    @Override
    public java.lang.Number getValue() {
        return value;
    }

    // ops


    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        if (other instanceof Number) {
            Number num;
            if (this instanceof Int && other instanceof Int)
                num = new Int(value.intValue() + ((Number) other).getValue().intValue());
            else
                num = new Float(value.floatValue() + ((Number) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> subtractedBy(BasicType other) {
        if (other instanceof Number) {
            Number num;
            if (this instanceof Int && other instanceof Int)
                num = new Int(value.intValue() - ((Number) other).getValue().intValue());
            else
                num = new Float(value.floatValue() - ((Number) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> multipliedBy(BasicType other) {
        if (other instanceof Number) {
            Number num;
            if (this instanceof Int && other instanceof Int)
                num = new Int(value.intValue() * ((Number) other).getValue().intValue());
            else
                num = new Float(value.floatValue() * ((Number) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> dividedBy(BasicType other) {
        if (other instanceof Number) {
            if (((Number) other).getValue().floatValue() == 0)
                return Tuple.of(null, new Error.RunTimeError(other.getPosStart(), other.getPosEnd(), "Division by 0", getContext()));
            Number num;
            java.lang.Number otherValue = ((Number) other).getValue();
            if (this instanceof Int && other instanceof Int && value.floatValue() / otherValue.floatValue() == value.intValue() / otherValue.intValue()) {
                num = new Int(value.intValue() / otherValue.intValue());
            } else
                num = new Float(value.floatValue() / otherValue.floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> toThePowerOf(BasicType other) {
        if (other instanceof Number) {
            Number num;
            java.lang.Number endResult = Math.pow(value.floatValue(), ((Number) other).getValue().floatValue());
            if (this instanceof Int && endResult.floatValue() == endResult.intValue())
                num = new Int(endResult.intValue());
            else
                num = new Float(endResult.floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> modulo(BasicType other) {
        if (other instanceof Number) {
            Number num;
            if (this instanceof Int && other instanceof Int)
                num = new Int(value.intValue() % ((Int) other).getValue());
            else
                num = new Float(value.floatValue() % ((Number) other).getValue().floatValue());
            return Tuple.of(num.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> lessThan(BasicType other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() < ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> greaterThan(BasicType other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() > ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> lessThanOrEqualTo(BasicType other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() <= ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> greaterThanOrEqualTo(BasicType other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() >= ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;

    }

    @Override
    public Tuple<BasicType, Error> equalTo(Value other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() == ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);

    }

    @Override
    public Tuple<BasicType, Error> notEqualTo(Value other) {
        if (other instanceof Number) {
            Boolean bool = new Boolean(value.floatValue() != ((Number) other).getValue().floatValue());
            return Tuple.of(bool.setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return Tuple.of(new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);

    }
}
