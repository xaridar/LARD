package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.Int;
import lscript.interpreting.types.Number;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class CeilBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("num", "val")));
    }

    @Override
    public String getName() {
        return "ceil";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new Int((int) Math.ceil(((Number) execCtx.getSymbolTable().get("val")).getValue().doubleValue())));
    }
}
