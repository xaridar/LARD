package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.LString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FormatBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("str", "text"), Tuple.of("list", "args")));
    }

    @Override
    public String getName() {
        return "format";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        StringBuilder builder = new StringBuilder();
        LList list = (LList) execCtx.getSymbolTable().get("args");
        LString string = (LString) execCtx.getSymbolTable().get("text");
        int count = 0;
        String val = string.getValue();
        int lastIdx = 0;
        while (lastIdx >= 0) {
            lastIdx = val.indexOf("{}", lastIdx);
            if (lastIdx >= 0) {
                count++;
                lastIdx+=2;
            }
        }
        if (list.getElements().size() != count) return new RTResult().failure(new Error.ArgumentError(fun.getPosStart(), fun.getPosEnd(), "Wrong number  of arguments passed into 'format': Expected " + count + ", got " + list.getElements().size(), execCtx));
        if (count == 0) return new RTResult().success(new LString(val));
        builder.append(val, 0, val.indexOf("{}"));
        int valIndex = 0;
        valIndex += val.indexOf("{}");
        for (int i = 0; i < count; i++) {
            builder.append(list.getElements().get(i));
            valIndex+=2;
            int indexToAppendTo = val.indexOf("{}", valIndex);
            if (indexToAppendTo == -1) indexToAppendTo = val.length();
            builder.append(val, valIndex, indexToAppendTo);
            valIndex = val.indexOf("{}", valIndex);
        }
        return new RTResult().success(new LString(builder.toString()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
