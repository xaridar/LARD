package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.Float;
import lscript.interpreting.types.Number;
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
        return new RTResult().success(new Float((float) Math.pow(((Number) execCtx.getSymbolTable().get("val")).getValue().doubleValue(), 1 / ((Number) execCtx.getSymbolTable().get("power")).getValue().doubleValue())));
    }
}
