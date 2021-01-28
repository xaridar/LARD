package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFloat;
import lscript.interpreting.types.LInt;
import lscript.interpreting.types.LNumber;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;

public class MinBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Arrays.asList(Tuple.of("int", "val1"), Tuple.of("int", "val2")), Arrays.asList(Tuple.of("float", "val1"), Tuple.of("float", "val2")), Arrays.asList(Tuple.of("int", "val1"), Tuple.of("float", "val2")), Arrays.asList(Tuple.of("float", "val1"), Tuple.of("int", "val2")));
    }

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LNumber value;
        if (execNum == 0) {
            value = new LInt(Math.min(((LInt) execCtx.getSymbolTable().get("val1")).getValue(), ((LInt) execCtx.getSymbolTable().get("val2")).getValue()));
        } else {
            value = new LFloat(Math.min(((LNumber) execCtx.getSymbolTable().get("val1")).getValue().floatValue(), ((LNumber) execCtx.getSymbolTable().get("val2")).getValue().floatValue()));
        }
        return new RTResult().success(value.setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
