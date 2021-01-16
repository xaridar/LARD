package lscript.interpreting.types;

import lscript.Tuple;
import lscript.errors.Error;

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

    public static LList from(Value val) {
        return new LList(java.util.List.of(val));
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
    public Tuple<Value, Error> elementAt(Value val) {
        if (!(val instanceof LInt))
            return Tuple.of(null, new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "lists can only be indexed by integer values.", context));
        int num = ((LInt) val).getValue();
        if (0 <= num && num <= elements.size() - 1)
            return Tuple.of(elements.get(((LInt) val).getValue()), null);
        if (num < 0 && -1*num <= elements.size())
            return Tuple.of(elements.get(num + elements.size()), null);
        return Tuple.of(null, new Error.IndexOutOfBoundsError(val.getPosStart(), val.getPosEnd(), "Index " + num + " out of range for length " + elements.size(), context));

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
            Tuple<Value, Error> otherElement = other.elementAt(new LInt(i));
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
            Tuple<Value, Error> otherElement = other.elementAt(new LInt(i));
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
