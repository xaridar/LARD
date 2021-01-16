package lscript.interpreting.types;

import lscript.Constants;
import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.lexing.Position;
import lscript.lexing.Token;

public abstract class BasicType extends Value {

    public BasicType(String type) {
        super(type);
    }

    @Override
    public abstract String toString();

    @Override
    public BasicType setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public BasicType setPos(Position posStart, Position posEnd) {
        this.posStart = posStart;
        this.posEnd = posEnd;
        return this;
    }

    @Override
    public Tuple<BasicType, Error> apply(Token opToken, Value otherType) {
        Tuple<BasicType, Error> res;
        if (!(otherType instanceof BasicType))
            return super.apply(opToken, otherType);
        BasicType other = (BasicType) otherType;
        switch (opToken.getType()) {
            case Constants.TT_PLUS:
                res = addedTo(other);
                break;
            case Constants.TT_MINUS:
                res = subtractedBy(other);
                break;
            case Constants.TT_MUL:
                res = multipliedBy(other);
                break;
            case Constants.TT_DIV:
                res = dividedBy(other);
                break;
            case Constants.TT_POW:
                res = toThePowerOf(other);
                break;
            case Constants.TT_MOD:
                res = modulo(other);
                break;
            case Constants.TT_LT:
                res = lessThan(other);
                break;
            case Constants.TT_GT:
                res = greaterThan(other);
                break;
            case Constants.TT_LEQ:
                res = lessThanOrEqualTo(other);
                break;
            case Constants.TT_GEQ:
                res = greaterThanOrEqualTo(other);
                break;
            case Constants.TT_BOOLEQ:
                res = Tuple.of(equalTo(other), null);
                break;
            case Constants.TT_NEQ:
                res = Tuple.of(notEqualTo(other), null);
                break;
            case Constants.TT_AND:
                res = and(other);
                break;
            case Constants.TT_PIPE:
                res = or(other);
                break;
            case Constants.TT_BANG:
                res = reversed();
                break;
            default:
                return null;
        }
        if (res == null) {
            res = super.apply(opToken, other);
        }
        return res;
    }

    // ops
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> subtractedBy(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> multipliedBy(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> dividedBy(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> toThePowerOf(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> modulo(BasicType other) {
        return null;
    }

    public Tuple<BasicType, Error> lessThan(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> greaterThan(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> lessThanOrEqualTo(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> greaterThanOrEqualTo(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> and(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> or(BasicType other) {
        return null;
    }
    public Tuple<BasicType, Error> reversed() {
        return null;
    }
}
