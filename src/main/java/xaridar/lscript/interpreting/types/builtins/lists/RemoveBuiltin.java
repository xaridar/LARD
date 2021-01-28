package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RemoveBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Arrays.asList(Tuple.of("list", "container"), Tuple.of("var", "value")), Arrays.asList(Tuple.of("map", "container"), Tuple.of("var", "value")));
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Value ret;
        if (execNum == 0) {
            LList list = (LList) execCtx.getSymbolTable().get("container");
            Optional<Value> v = list.getElements().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst();
            ret = new LBoolean(v.isPresent());
            v.ifPresent(value -> list.getElements().remove(value));
        } else if (execNum == 1) {
            LMap map = (LMap) execCtx.getSymbolTable().get("container");
            Optional<Value> v = map.getMap().keySet().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst();
            ret = v.isPresent() ? map.getMap().get(v.get()) : NullType.Null;
            v.ifPresent(val -> {
                map.getMap().remove(val);
            });
        } else {
            return null;
        }
        return new RTResult().success(ret.setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
