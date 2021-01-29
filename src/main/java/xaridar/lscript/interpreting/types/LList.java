package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;

import java.util.Collections;
import java.util.stream.Collectors;

public class LList extends BasicType {

    private final java.util.List<Value> elements;

    public LList(java.util.List<Value> elements) {
        super("list");
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "[" + elements.stream().map(Value::toString).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public LList copy() {
        LList l = new LList(elements);
        l.setPos(getPosStart(), getPosEnd());
        l.setContext(getContext());
        return l;
    }

    public static RunTimeResult from(Value val) {
        return new RunTimeResult().success(new LList(Collections.singletonList(val)).setContext(val.getContext()).setPos(val.getPosStart(), val.getPosEnd()));
    }

    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        LList newList = copy();
        if (other instanceof LList)
            newList.getElements().addAll(((LList) other).elements);
        else
            newList.getElements().add(other);
        return Tuple.of(newList, null);
    }

    public java.util.List<Value> getElements() {
        return elements;
    }

    @Override
    public java.util.List<Value> getValue() {
        return elements;
    }

    @Override
    public Tuple<Value, Error> elementAt(Value startIndex, Value endIndex) {
        if (!(startIndex instanceof LInt) || !(endIndex instanceof LInt))
            return Tuple.of(null, new Error.RunTimeError(startIndex.getPosStart(), endIndex.getPosEnd(), "lists can only be indexed or sliced by integer values.", context));
        int startNum = ((LInt) startIndex).getValue();
        int endNum = ((LInt) endIndex).getValue();
        if (startNum < 0)
            startNum = startNum + elements.size();
        if (startNum > elements.size()) return Tuple.of(null, new Error.IndexOutOfBoundsError(startIndex.getPosStart(), startIndex.getPosEnd(), "Index " + startIndex.getValue() + " out of range for length " + elements.size(), context));
        if (endNum < 0)
            endNum = endNum + elements.size();
        if (endNum > elements.size()) return Tuple.of(null, new Error.IndexOutOfBoundsError(endIndex.getPosStart(), endIndex.getPosEnd(), "Index " + endIndex.getValue() + " out of range for length " + elements.size(), context));
        if (startNum > endNum) return Tuple.of(null, new Error.RunTimeError(startIndex.getPosStart(), endIndex.getPosEnd(), "Start index cannot be greater than end index", context));
        if (startNum == endNum) {
            return Tuple.of(elements.get(startNum), null);
        } else {
            return Tuple.of(new LList(elements.subList(startNum, endNum)), null);
        }
    }

    @Override
    public Tuple<Value, Error> setElementAt(Value index, Value val) {
        if (!(index instanceof LInt))
            return Tuple.of(null, new Error.RunTimeError(index.getPosStart(), index.getPosEnd(), "lists can only be indexed by integer values.", context));
        int num = ((LInt) index).getValue();
        if (0 <= num && num <= elements.size() - 1)
            return Tuple.of(elements.set(num, val), null);
        if (num < 0 && -1*num <= elements.size())
            return Tuple.of(elements.set(num + elements.size(), val), null);
        return Tuple.of(null, new Error.IndexOutOfBoundsError(index.getPosStart(), index.getPosEnd(), "Index " + num + " out of range for length " + elements.size(), context));

    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LList))
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
            Value val = elements.get(i);
            Tuple<Value, Error> otherElement = other.elementAt(new LInt(i), new LInt(i));
            if (otherElement.getRight() != null)
                return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            LBoolean equal = val.equalTo(otherElement.getLeft());
            if (!equal.isTrue()) {
                res = false;
            }
        }
        return (LBoolean) new LBoolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof LList))
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
            Value val = elements.get(i);
            Tuple<Value, Error> otherElement = other.elementAt(new LInt(i), new LInt(i));
            if (otherElement.getRight() != null)
                return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            LBoolean equal = val.equalTo(otherElement.getLeft());
            if (equal.isTrue()) {
                res = false;
            }
        }
        return (LBoolean) new LBoolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
