package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.LString;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SplitBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.singletonList(Tuple.of("str", "toSplit")), Arrays.asList(Tuple.of("str", "toSplit"), Tuple.of("str", "splitBy")));
    }

    @Override
    public String getName() {
        return "split";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LString toSplit = (LString) execCtx.getSymbolTable().get("toSplit");
        String toSplitStr = toSplit.getValue();
        String splitBy = "";
        if (execNum == 1) {
            splitBy = ((LString) execCtx.getSymbolTable().get("splitBy")).getValue();
        }
        return new RTResult().success(new LList(Arrays.stream(toSplitStr.split(splitBy)).map(str -> new LString(str).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd())).collect(Collectors.toList())).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
    }
}
