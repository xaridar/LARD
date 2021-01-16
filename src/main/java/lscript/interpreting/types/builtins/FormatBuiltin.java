package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.Str;

import java.util.List;

public class FormatBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "text"), Tuple.of("list", "args")));
    }

    @Override
    public String getName() {
        return "format";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        StringBuilder builder = new StringBuilder();
        lscript.interpreting.types.List list = (lscript.interpreting.types.List) execCtx.getSymbolTable().get("args");
        Str string = (Str) execCtx.getSymbolTable().get("text");
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
        if (list.getElements().size() != count) return new RTResult().failure(new Error.RunTimeError(fun.getPosStart(), fun.getPosEnd(), "Wrong number  of arguments passed into 'format': Expected " + count + ", got " + list.getElements().size(), execCtx));
        if (count == 0) return new RTResult().success(new Str(val));
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
        return new RTResult().success(new Str(builder.toString()));
    }
}
