package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFloat;
import lscript.interpreting.types.LNumber;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class RootBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("num", "val"), Tuple.of("num", "power")));
    }

    @Override
    public String getName() {
        return "root";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new LFloat((float) Math.pow(((LNumber) execCtx.getSymbolTable().get("val")).getValue().doubleValue(), 1 / ((LNumber) execCtx.getSymbolTable().get("power")).getValue().doubleValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
