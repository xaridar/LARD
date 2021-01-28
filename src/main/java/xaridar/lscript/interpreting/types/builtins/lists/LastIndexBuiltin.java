package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class LastIndexBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(
                Arrays.asList(Tuple.of("str", "toIndex"), Tuple.of("str", "val")),
                Arrays.asList(Tuple.of("list", "toIndex"), Tuple.of("var", "val"))
        );
    }

    @Override
    public String getName() {
        return "lastindexof";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
        Value toIndex = execCtx.getSymbolTable().get("toIndex");
        Value val = execCtx.getSymbolTable().get("val");
        int index = -1;
        if (execNum == 0) {
            String s = ((LString) toIndex).getValue();
            index = s.lastIndexOf(((LString) val).getValue());
        } else if (execNum == 1) {
            List<Value> l = ((LList) toIndex).getValue();
            Stream<Value> stream = l.stream().filter(value -> val.equalTo(value).isTrue());
            Optional<Value> v = stream.skip(stream.count() - 1).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else {
            return null;
        }
        return res.success(new LInt(index).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
