package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFloat;

import java.util.List;
import java.util.Random;

public class RandBuiltin implements IExecutable {
    Random random = new Random();
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of());
    }

    @Override
    public String getName() {
        return "rand";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new LFloat(random.nextFloat()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
    }
}
