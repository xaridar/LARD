package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LInt;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.Value;
import lscript.interpreting.types.builtins.IExecutable;

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
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
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
