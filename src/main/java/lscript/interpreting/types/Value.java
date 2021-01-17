package lscript.interpreting.types;

import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.lexing.Position;
import lscript.lexing.Token;
import lscript.interpreting.RTResult;

import java.util.List;

/**
 * Holds a single value of any type, including int, float, boolean, list, map, function, file, and nulltype.
 */
public abstract class Value {

    protected Context context;
    protected Position posStart;
    protected Position posEnd;
    protected String type;

    /**
     * @param type - The type associated with the value. (ex. 'str', 'int')
     */
    public Value(String type) {
        this.posStart = null;
        this.posEnd = null;
        this.context = null;
        this.type = type;
    }

    /**
     * @return The end Position of the Value in the text.
     */
    public Position getPosEnd() {
        return posEnd;
    }

    /**
     * @return The start Position of the value in the text.
     */
    public Position getPosStart() {
        return posStart;
    }

    /**
     * @return The Context the Value exists in.
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return The Value's type as a String.
     */
    public String getType() {
        return type;
    }

    /**
     * @return A copy of the Value.
     */
    public abstract Value copy();

    /**
     * @return The value of this Value, if it has one. Can return null.
     */
    public abstract Object getValue();

    /**
     * @param context - The Context to set for this Value.
     * @return this Value.
     */
    public Value setContext(Context context) {
        this.context = context;
        return this;
    }

    /**
     * @param posStart - The start Position to set for this Value.
     * @param posEnd - The end Position to set for this Value.
     * @return this Value.
     */
    public Value setPos(Position posStart, Position posEnd) {
        this.posEnd = posEnd;
        this.posStart = posStart;
        return this;
    }

    /**
     * Applies an operation to two values, called by BinaryOperationNode.
     * @param opToken - A Token containing the operation to apply.
     * @param other - Another value to perform this operation on.
     * @return An RTResult containing either a Value or an Error; without overriding, it returns an UnsupportedOperationError.
     */
    public Tuple<BasicType, Error> apply(Token opToken, Value other) {
        return Tuple.of(null, new Error.UnsupportedOperationError(opToken.getPosStart(), opToken.getPosEnd(), "'" + opToken.getOpChar() + "' unsupported " + (other != null ? "between '" + getType() + "' and '" + other.getType() + "'" : "for type '" + getType() + "'"), context));
    }

    /**
     * Accesses the element at an index, called by IndexNode.
     * @param value - A Value to index this value at.
     * @return An RTResult containing either a Value or an Error; without overriding, it returns an UnsupportedOperationError.
     */
    public Tuple<Value, Error> elementAt(Value value) {
        return Tuple.of(null, new Error.UnsupportedOperationError(value.getPosStart(), value.getPosEnd(), "Indexing unsupported for type '" + getType() + "'", context));
    }

    /**
     * Sets the element at an index, called by SetIndexNode.
     * @param index - A Value to index this value at.
     * @param value - A Value to set this Value equal to at the provided index.
     * @return An RTResult containing either a Value or an Error; without overriding, it returns an UnsupportedOperationError.
     */
    public Tuple<Value, Error> setElementAt(Value index, Value value) {
        return Tuple.of(null, new Error.UnsupportedOperationError(value.getPosStart(), value.getPosEnd(), "Setting elements unsupported for type '" + getType() + "'", context));
    }

    /**
     * Called from any CallNode on a function.
     * @param args - A list of args for execution of a function.
     * @return An RTResult containing either a Value or an Error; without overriding, it returns an UnsupportedOperationError.
     */
    public RTResult execute(List<Value> args) {
        return new RTResult().failure(new Error.UnsupportedOperationError(posStart, posEnd, "Expected function", context));
    }

    /**
     * @param expectedType - The type to set for this Value.
     */
    public void setType(String expectedType) {
        type = expectedType;
    }


    /**
     * @param other - Another Value to compare this one to.
     * @return an LBoolean containing whether this Value is equivalent to other.
     */
    public abstract LBoolean equalTo(Value other);

    /**
     * @param other - Another Value to compare this one to.
     * @return an LBoolean containing whether this Value is not equivalent to other.
     */
    public abstract LBoolean notEqualTo(Value other);


    /**
     * @return a boolean representing whether this value's boolean representation evaluates to true.
     */
    public boolean isTrue() {
        return !getValue().equals(NullType.Null);
    }

}
