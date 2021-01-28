package xaridar.lscript.interpreting.types.builtins.random;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LFloat;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandBuiltin implements IExecutable {
    Random random = new Random();
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "rand";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RunTimeResult().success(new LFloat(random.nextFloat()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
    }
}
