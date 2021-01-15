package jav.basic.types.builtins.math;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Float;
import jav.basic.types.Number;
import jav.basic.types.builtins.IExecutable;

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
