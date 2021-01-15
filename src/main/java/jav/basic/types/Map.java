package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

import java.util.HashMap;

public class Map extends BasicType {
    private final java.util.HashMap<Value, Value> map;

    public Map(java.util.HashMap<Value, Value> map) {
        super("map");
        this.map = map;
    }

    public HashMap<Value, Value> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Map copy() {
        Map m = new Map(map);
        m.setContext(getContext());
        m.setPos(getPosStart(), getPosEnd());
        return m;
    }

    @Override
    public java.util.HashMap<Value, Value> getValue() {
        return map;
    }

    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        Map newMap = copy();
        if (other instanceof Map) {
            newMap.getMap().putAll(((Map) other).getMap());
            return Tuple.of(newMap, null);
        }
        return null;
    }

    @Override
    public Tuple<Value, Error> elementAt(Value val) {
        final Value[] element = {null};
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<BasicType, Error> equal = key.equalTo(val);
            if (equal.getRight() != null) return Tuple.of(null, equal.getRight());
            if (equal.getLeft().isTrue().getLeft()) {
                element[0] = value;
                break;
            }
        }
        if (element[0] == null) {
            return Tuple.of(null, new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "No value found for key " + val, context));
        }
        return Tuple.of(element[0], null);
    }

    @Override
    public Tuple<BasicType, Error> equalTo(Value other) {
        if (!(other instanceof Map))
            return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key);
            if (otherElement.getRight() != null)
                return Tuple.of(new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
            Tuple<BasicType, Error> equal = value.equalTo(otherElement.getLeft());
            if (equal.getRight() != null) return Tuple.of(null, equal.getRight());
            if (!equal.getLeft().isTrue().getLeft()) {
                res = false;
            }
        }
        return Tuple.of(new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> notEqualTo(Value other) {
        if (!(other instanceof Map))
            return Tuple.of(new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key);
            if (otherElement.getRight() != null)
                return Tuple.of(new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
            Tuple<BasicType, Error> equal = value.equalTo(otherElement.getLeft());
            if (equal.getRight() != null) return Tuple.of(null, equal.getRight());
            if (equal.getLeft().isTrue().getLeft()) {
                res = false;
            }
        }
        return Tuple.of(new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }
}
