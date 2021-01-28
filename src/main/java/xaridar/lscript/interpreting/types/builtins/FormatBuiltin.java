package xaridar.lscript.interpreting.types.builtins;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LList;
import xaridar.lscript.interpreting.types.LString;

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
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
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
        if (list.getElements().size() != count) return new RunTimeResult().failure(new Error.ArgumentError(fun.getPosStart(), fun.getPosEnd(), "Wrong number  of arguments passed into 'format': Expected " + count + ", got " + list.getElements().size(), execCtx));
        if (count == 0) return new RunTimeResult().success(new LString(val));
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
        return new RunTimeResult().success(new LString(builder.toString()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
