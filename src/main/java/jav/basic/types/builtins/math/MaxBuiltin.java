package jav.basic.types.builtins.math;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Float;
import jav.basic.types.Int;
import jav.basic.types.Number;
import jav.basic.types.builtins.IExecutable;

import java.util.List;

public class MaxBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("int", "val1"), Tuple.of("int", "val2")), List.of(Tuple.of("float", "val1"), Tuple.of("float", "val2")), List.of(Tuple.of("int", "val1"), Tuple.of("float", "val2")), List.of(Tuple.of("float", "val1"), Tuple.of("int", "val2")));
    }

    @Override
    public String getName() {
        return "max";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Number value = null;
        if (execNum == 0) {
            value = new Int(Math.max(((Int) execCtx.getSymbolTable().get("val1")).getValue(), ((Int) execCtx.getSymbolTable().get("val2")).getValue()));
        } else {
            value = new Float(Math.max(((Number) execCtx.getSymbolTable().get("val1")).getValue().floatValue(), ((Number) execCtx.getSymbolTable().get("val2")).getValue().floatValue()));
        }
        return new RTResult().success(value);
    }
}
