package xaridar.lscript.interpreting.types.builtins.math;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LFloat;
import xaridar.lscript.interpreting.types.LInt;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.singletonList(Tuple.of("int", "val")), Collections.singletonList(Tuple.of("float", "val")));
    }

    @Override
    public String getName() {
        return "abs";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 0) {
            return new RunTimeResult().success(new LInt(Math.abs(((LInt) execCtx.getSymbolTable().get("val")).getValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        } else if (execNum == 1) {
            return new RunTimeResult().success(new LFloat(Math.abs(((LFloat) execCtx.getSymbolTable().get("val")).getValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        }
        return null;
    }
}
