package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;

import java.util.Collections;
import java.util.List;

public class QuitBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.emptyList());
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
