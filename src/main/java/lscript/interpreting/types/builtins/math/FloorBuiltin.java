package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LInt;
import lscript.interpreting.types.LNumber;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class FloorBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("num", "val")));
    }

    @Override
    public String getName() {
        return "floor";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new LInt((int) Math.floor(((LNumber) execCtx.getSymbolTable().get("val")).getValue().doubleValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
