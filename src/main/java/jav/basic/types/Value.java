package jav.basic.types;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;
import jav.basic.Position;
import jav.basic.Token;
import jav.basic.results.RTResult;

import java.util.List;

public abstract class Value {

    protected Context context;
    protected Position posStart;
    protected Position posEnd;
    protected String type;

    public Value(String type) {
        this.posStart = null;
        this.posEnd = null;
        this.context = null;
        this.type = type;
    }

    public Position getPosEnd() {
        return posEnd;
    }

    public Position getPosStart() {
        return posStart;
    }

    public Context getContext() {
        return context;
    }

    public String getType() {
        return type;
    }

    public abstract Value copy();

    public abstract Object getValue();

    public Value setContext(Context context) {
        this.context = context;
        return this;
    }

    public Value setPos(Position posStart, Position posEnd) {
        this.posEnd = posEnd;
        this.posStart = posStart;
        return this;
    }

    public Tuple<BasicType, Error> apply(Token opToken, Value other) {
        return Tuple.of(null, new Error.UnsupportedOperationError(opToken.getPosStart(), opToken.getPosEnd(), "'" + opToken.getOpChar() + "' unsupported " + (other != null ? "between '" + getType() + "' and '" + other.getType() + "'" : "for type '" + getType() + "'"), context));
    }

    public Tuple<Value, Error> elementAt(Value value) {
        return Tuple.of(null, new Error.UnsupportedOperationError(value.getPosStart(), value.getPosEnd(), "Indexing unsupported for type '" + getType() + "'", context));
    }

    public Tuple<Value, Error> setElementAt(Value index, Value value) {
        return Tuple.of(null, new Error.UnsupportedOperationError(value.getPosStart(), value.getPosEnd(), "Setting elements unsupported for type '" + getType() + "'", context));
    }

    public RTResult execute(List<Value> args) {
        return new RTResult().failure(new Error.UnsupportedOperationError(posStart, posEnd, "Expected function", context));
    }

    public void setType(String expectedType) {
        type = expectedType;
    }


    public abstract Tuple<BasicType, Error> equalTo(Value other);

    public abstract Tuple<BasicType, Error> notEqualTo(Value other);


    public Tuple<java.lang.Boolean, Error> isTrue() {
        return Tuple.of(!getValue().equals(NullType.Null), null);
    }

}
