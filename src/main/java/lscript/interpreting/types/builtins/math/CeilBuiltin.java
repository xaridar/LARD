package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LInt;
import lscript.interpreting.types.LNumber;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Collections;
import java.util.List;

public class CeilBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("num", "val")));
    }

    @Override
    public String getName() {
        return "ceil";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new LInt((int) Math.ceil(((LNumber) execCtx.getSymbolTable().get("val")).getValue().doubleValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
