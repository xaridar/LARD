package xaridar.lscript.interpreting.types;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LString extends BasicType {

    private final String value;

    public LString(String value) {
        super("str");
        this.value = value;
    }

    public LString(String value, Context context) {
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
    public LString copy() {
        LString s = new LString(value);
        s.setPos(getPosStart(), getPosEnd());
        s.setContext(getContext());
        return s;
    }

    @SuppressWarnings("unused")
    public static RunTimeResult from(Value val) {
        return new RunTimeResult().success(new LString(val.getValue().toString()).setContext(val.getContext()).setPos(val.getPosStart(), val.getPosEnd()));
    }

    @Override
    public String getValue() {
        return value;
    }

    // ops


    @Override
    public Tuple<BasicType, Error> addedTo(BasicType other) {
        return Tuple.of(new LString(value + other.toString()).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
    }

    @Override
    public Tuple<BasicType, Error> multipliedBy(BasicType other) {
        if (other instanceof LInt) {
            return Tuple.of(new LString(new String(new char[(((LInt) other).getValue())]).replace("\0", value)).setContext(getContext()).setPos(getPosStart(), getPosEnd()), null);
        }
        return null;
    }

    @Override
    public Tuple<Value, Error> elementAt(Value startIndex, Value endIndex) {
        if (!(startIndex instanceof LInt) || !(endIndex instanceof LInt))
            return Tuple.of(null, new Error.RunTimeError(startIndex.getPosStart(), endIndex.getPosEnd(), "lists can only be indexed or sliced by integer values.", context));
        int startNum = ((LInt) startIndex).getValue();
        int endNum = ((LInt) endIndex).getValue();
        if (startNum < 0)
            startNum = startNum + value.length();
        if (startNum > value.length()) return Tuple.of(null, new Error.IndexOutOfBoundsError(startIndex.getPosStart(), startIndex.getPosEnd(), "Index " + startIndex.getValue() + " out of range for length " + value.length(), context));
        if (endNum < 0)
            endNum = endNum + value.length();
        if (endNum > value.length()) return Tuple.of(null, new Error.IndexOutOfBoundsError(endIndex.getPosStart(), endIndex.getPosEnd(), "Index " + endIndex.getValue() + " out of range for length " + value.length(), context));
        if (startNum > endNum) return Tuple.of(null, new Error.RunTimeError(startIndex.getPosStart(), endIndex.getPosEnd(), "Start index cannot be greater than end index", context));
        if (startNum == endNum) {
            return Tuple.of(new LString(String.valueOf(value.charAt(startNum))), null);
        } else {
            return Tuple.of(new LString(value.substring(startNum, endNum)), null);
        }
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LString)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(value.equals(other.getValue())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        if (!(other instanceof LString)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(!value.equals(other.getValue())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
