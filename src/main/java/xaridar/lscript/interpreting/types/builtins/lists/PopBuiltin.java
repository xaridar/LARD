package xaridar.lscript.interpreting.types.builtins.lists;

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LInt;
import xaridar.lscript.interpreting.types.LList;
import xaridar.lscript.interpreting.types.Value;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PopBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("list", "container"), Tuple.of("int", "index")));
    }

    @Override
    public String getName() {
        return "pop";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RunTimeResult res = new RunTimeResult();
        LList list = (LList) execCtx.getSymbolTable().get("container");
        LInt index = (LInt) execCtx.getSymbolTable().get("index");
        int idxVal = index.getValue();
        if (idxVal < 0) {
            idxVal += list.getElements().size();
        }
        if (idxVal < 0 || idxVal >= list.getElements().size()) {
            return res.failure(new Error.IndexOutOfBoundsError(index.getPosStart(), index.getPosEnd(), "Index " + index.getValue() + " out of bounds for length " + list.getElements().size(), fun.getContext()));
        }
        Value val = list.getElements().remove(idxVal).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext());
        return res.success(val);
    }
}
