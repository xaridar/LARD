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
            Boolean equal = key.equalTo(val);
            if (equal.isTrue()) {
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
    public Boolean equalTo(Value other) {
        if (!(other instanceof Map))
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key);
            if (otherElement.getRight() != null)
                return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            Boolean equal = value.equalTo(otherElement.getLeft());
            if (!equal.isTrue()) {
                res = false;
            }
        }
        return (Boolean) new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        if (!(other instanceof Map))
            return (Boolean) new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key);
            if (otherElement.getRight() != null)
                return (Boolean) new Boolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            Boolean equal = value.equalTo(otherElement.getLeft());
            if (equal.isTrue()) {
                res = false;
            }
        }
        return (Boolean) new Boolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
