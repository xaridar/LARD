package lscript.interpreting.types;

import lscript.Tuple;
import lscript.errors.Error;

import java.util.HashMap;

public class LMap extends BasicType {
    private final java.util.HashMap<Value, Value> map;

    public LMap(java.util.HashMap<Value, Value> map) {
        super("map");
        this.map = map;
    }

    public HashMap<Value, Value> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString().replace("=", ":");
    }

    @Override
    public LMap copy() {
        LMap m = new LMap(map);
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
        LMap newMap = copy();
        if (other instanceof LMap) {
            newMap.getMap().putAll(((LMap) other).getMap());
            return Tuple.of(newMap, null);
        }
        return null;
    }

    @Override
    public Tuple<Value, Error> elementAt(Value startIndex, Value endIndex) {
        if (startIndex.notEqualTo(endIndex).isTrue()) return Tuple.of(null, new Error.UnsupportedOperationError(startIndex.getPosStart(), endIndex.getPosEnd(), "Cannot slice map", getContext()));
        Value element = null;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            LBoolean equal = key.equalTo(startIndex);
            if (equal.isTrue()) {
                element = value;
                break;
            }
        }
        if (element == null) {
            return Tuple.of(null, new Error.RunTimeError(startIndex.getPosStart(), startIndex.getPosEnd(), "No value found for key " + startIndex, context));
        }
        return Tuple.of(element, null);
    }

    @Override
    public Tuple<Value, Error> setElementAt(Value startIndex, Value value) {
        Value lastVal = NullType.Null;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            LBoolean equal = key.equalTo(startIndex);
            if (equal.isTrue()) {
                lastVal = map.remove(key);
                break;
            }
        }
        map.put(startIndex, value);
        return Tuple.of(lastVal, null);
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LMap))
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key, key);
            if (otherElement.getRight() != null)
                return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            LBoolean equal = value.equalTo(otherElement.getLeft());
            if (!equal.isTrue()) {
                res = false;
            }
        }
        return (LBoolean) new LBoolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof LMap))
            return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        boolean res = true;
        for (java.util.Map.Entry<Value, Value> entry : map.entrySet()) {
            Value key = entry.getKey();
            Value value = entry.getValue();
            Tuple<Value, Error> otherElement = other.elementAt(key, key);
            if (otherElement.getRight() != null)
                return (LBoolean) new LBoolean(true).setContext(getContext()).setPos(getPosStart(), getPosEnd());
            LBoolean equal = value.equalTo(otherElement.getLeft());
            if (equal.isTrue()) {
                res = false;
            }
        }
        return (LBoolean) new LBoolean(res).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
