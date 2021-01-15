package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

import java.util.stream.Collectors;

public class List extends BasicType {

    private final java.util.List<Value> elements;

    public List(java.util.List<Value> elements) {
        super("list");
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "[" + elements.stream().map(Value::toString).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public List copy() {
        List l = new List(elements);
        l.setPos(getPosStart(), getPosEnd());
        l.setContext(getContext());
        return l;
    }

    public static List from(Value val) {
        return new List(java.util.List.of(val));
    }

    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        List newList = copy();
        if (other instanceof List)
            newList.getElements().addAll(((List) other).elements);
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
        if (!(val instanceof Int))
            return Tuple.of(null, new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "lists can only be indexed by integer values.", context));
        int num = ((Int) val).getValue();
        if (0 <= num && num <= elements.size() - 1)
            return Tuple.of(elements.get(((Int) val).getValue()), null);
        if (num < 0 && -1*num <= elements.size())
            return Tuple.of(elements.get(num + elements.size()), null);
        return Tuple.of(null, new Error.IndexOutOfBoundsError(val.getPosStart(), val.getPosEnd(), "Index " + num + " out of range for length " + elements.size(), context));

    }

    @Override
    public Tuple<Value, Error> setElementAt(Value index, Value val) {
        if (!(index instanceof Int))
            return Tuple.of(null, new Error.RunTimeError(index.getPosStart(), index.getPosEnd(), "lists can only be indexed by integer values.", context));
        int num = ((Int) index).getValue();
        if (0 <= num && num <= elements.size() - 1)
            return Tuple.of(elements.set(num, val), null);
        if (num < 0 && -1*num <= elements.size())
            return Tuple.of(elements.set(num + elements.size(), val), null);
        return Tuple.of(null, new Error.IndexOutOfBoundsError(index.getPosStart(), index.getPosEnd(), "Index " + num + " out of range for length " + elements.size(), context));

    }

    @Override
    public Tuple<BasicType, Error> equalTo(Value other) {
        if (!(other instanceof List))
            return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        boolean res = true;
        for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
            Value val = elements.get(i);
            Tuple<Value, Error> otherElement = other.elementAt(new Int(i));
            if (otherElement.getRight() != null)
                return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
            Tuple<BasicType, Error> equal = val.equalTo(otherElement.getLeft());
            if (equal.getRight() != null) return Tuple.of(null, equal.getRight());
            if (!equal.getLeft().isTrue().getLeft()) {
                res = false;
            }
        }
        return Tuple.of(new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> notEqualTo(Value other) {
        if (!(other instanceof List))
            return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        boolean res = true;
        for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
            Value val = elements.get(i);
            Tuple<Value, Error> otherElement = other.elementAt(new Int(i));
            if (otherElement.getRight() != null)
                return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
            Tuple<BasicType, Error> equal = val.equalTo(otherElement.getLeft());
            if (equal.getRight() != null) return Tuple.of(null, equal.getRight());
            if (equal.getLeft().isTrue().getLeft()) {
                res = false;
            }
        }
        return Tuple.of(new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }
}
