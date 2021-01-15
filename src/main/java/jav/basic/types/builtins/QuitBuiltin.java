package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;

import java.util.List;

public class QuitBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of());
    }

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (fun.getPosStart().getFn().equals("<stdin>")) {
            System.exit(0);
        }
        return new RTResult().success(NullType.Void);
    }
}
