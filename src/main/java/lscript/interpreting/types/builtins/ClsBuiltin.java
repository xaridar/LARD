package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "cls";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        System.out.println("Sorry, cls is not supported in java implementation.");
        return new RTResult().success(NullType.Null);
    }
}
