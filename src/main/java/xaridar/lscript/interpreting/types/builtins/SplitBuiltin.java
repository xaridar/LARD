package xaridar.lscript.interpreting.types.builtins;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LList;
import xaridar.lscript.interpreting.types.LString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SplitBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.singletonList(Tuple.of("str", "toSplit")), Arrays.asList(Tuple.of("str", "toSplit"), Tuple.of("str", "regex")));
    }

    @Override
    public String getName() {
        return "split";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LString toSplit = (LString) execCtx.getSymbolTable().get("toSplit");
        String toSplitStr = toSplit.getValue();
        String splitBy = "";
        if (execNum == 1) {
            splitBy = ((LString) execCtx.getSymbolTable().get("regex")).getValue();
        }
        return new RunTimeResult().success(new LList(Arrays.stream(toSplitStr.split(splitBy)).map(str -> new LString(str).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd())).collect(Collectors.toList())).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
    }
}
