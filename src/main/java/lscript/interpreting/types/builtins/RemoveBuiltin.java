package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.Boolean;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;
import lscript.interpreting.types.Value;

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
            lscript.interpreting.types.List list = (lscript.interpreting.types.List) execCtx.getSymbolTable().get("container");
            list.getElements().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst().ifPresent(val -> ret[0] = new Boolean(list.getElements().remove(val)));
        } else if (execNum == 1) {
            lscript.interpreting.types.Map map = (lscript.interpreting.types.Map) execCtx.getSymbolTable().get("container");
            map.getMap().keySet().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).isTrue()).findFirst().ifPresent(val -> {
                ret[0] = map.getMap().get(val);
                map.getMap().remove(val);
            });
        }
        return new RTResult().success(ret[0]);
    }
}
