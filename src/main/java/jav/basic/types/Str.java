package jav.basic.types;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Str extends BasicType {

    private String value;

    public Str(String value) {
        super("str");
        this.value = value;
    }

    public Str(String value, Context context) {
        super("str");
        setContext(context);
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("((?<=%)\\w*)")
                .matcher(value);
        while (m.find()) {
            allMatches.add(m.group());
        }
        StringBuilder builder = new StringBuilder();
        int index = 0;
        if (allMatches.size() > 0)
            builder.append(value, index, value.indexOf("%"));
        else {
            this.value = value;
            return;
        }
        index += value.substring(index, value.indexOf("%")).length();
        for (String arg : allMatches) {
            if (getContext().getSymbolTable().get(arg) == null) {
                this.value = value;
                return;
            }
            builder.append(getContext().getSymbolTable().get(arg).toString());
            index++;
            index += arg.length();
            int end = value.indexOf("%", index);
            if (end == -1) end = value.length();
            builder.append(value, index, end);
            index += value.substring(index, end).length();
        }
        this.value = builder.toString();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Str copy() {
        Str s = new Str(value);
        s.setPos(getPosStart(), getPosEnd());
        s.setContext(getContext());
        return s;
    }

    public static Str from(Value val) {
        return new Str(val.getValue().toString());
    }

    @Override
    public String getValue() {
        return value;
    }

    // ops


    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        return Tuple.of(new Str(value + other.toString()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> multipliedBy(BasicType other) {
        if (other instanceof Int) {
            return Tuple.of(new Str(value.repeat(((Int) other).getValue())).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;
    }

    @Override
    public Tuple<Value, Error> elementAt(Value val) {
        if (!(val instanceof Int))
            return Tuple.of(null, new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "str values can only be indexed by integer values.", context));
        int num = ((Int) val).getValue();
        if (0 <= num && num < value.length() - 1)
            return Tuple.of(new Str(Character.toString(value.charAt(((Int) val).getValue()))).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        if (num < 0 && -1*num < value.length())
            return Tuple.of(new Str(Character.toString(value.charAt(num + value.length()))).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        return Tuple.of(null, new Error.IndexOutOfBoundsError(val.getPosStart(), val.getPosEnd(), "Index " + num + " out of range for length " + value.length(), context));
    }

    @Override
    public Boolean equalTo(Value other) {
        if (!(other instanceof Str)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(value.equals(other.getValue())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        if (!(other instanceof Str)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(!value.equals(other.getValue())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
