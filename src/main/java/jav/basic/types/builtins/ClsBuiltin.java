package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;

import java.util.List;

public class ClsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of());
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
