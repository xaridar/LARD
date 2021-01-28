package xaridar.lscript.interpreting.types.builtins.math;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LFloat;
import xaridar.lscript.interpreting.types.LNumber;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RootBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("num", "val"), Tuple.of("num", "power")));
    }

    @Override
    public String getName() {
        return "root";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RunTimeResult().success(new LFloat((float) Math.pow(((LNumber) execCtx.getSymbolTable().get("val")).getValue().doubleValue(), 1 / ((LNumber) execCtx.getSymbolTable().get("power")).getValue().doubleValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
