package jav.basic.types.builtins;

import jav.Shell;
import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;
import jav.basic.types.Str;

import java.util.List;

public class EvalBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "text")));
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Shell.run(fun.getPosStart().getFn(), ((Str) execCtx.getSymbolTable().get("text")).getValue());
        return new RTResult().success(NullType.Void);
    }
}
