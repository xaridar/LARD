package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;

import java.util.List;

public class RemoveBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("list", "container"), Tuple.of("var", "value")), List.of(Tuple.of("map", "container"), Tuple.of("var", "value")));
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        final Value[] ret = {NullType.Null};
        if (execNum == 0) {
            LList list = (LList) execCtx.getSymbolTable().get("container");
            list.getElements().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst().ifPresent(val -> ret[0] = new LBoolean(list.getElements().remove(val)));
        } else if (execNum == 1) {
            LMap map = (LMap) execCtx.getSymbolTable().get("container");
            map.getMap().keySet().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst().ifPresent(val -> {
                ret[0] = map.getMap().get(val);
                map.getMap().remove(val);
            });
        }
        return new RTResult().success(ret[0].setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
