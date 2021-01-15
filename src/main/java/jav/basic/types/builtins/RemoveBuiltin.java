package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.Boolean;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;
import jav.basic.types.Value;

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
            jav.basic.types.List list = (jav.basic.types.List) execCtx.getSymbolTable().get("container");
            list.getElements().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).getLeft().isTrue().getLeft()).findFirst().ifPresent(val -> {
                ret[0] = new Boolean(list.getElements().remove(val));
            });
        } else if (execNum == 1) {
            jav.basic.types.Map map = (jav.basic.types.Map) execCtx.getSymbolTable().get("container");
            map.getMap().keySet().stream().filter(val -> val.equalTo(execCtx.getSymbolTable().get("value")).getLeft().isTrue().getLeft()).findFirst().ifPresent(val -> {
                ret[0] = map.getMap().get(val);
                map.getMap().remove(val);
            });
        }
        return new RTResult().success(ret[0]);
    }
}
