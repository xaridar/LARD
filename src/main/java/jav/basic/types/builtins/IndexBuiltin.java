package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;

import java.util.List;
import java.util.stream.Collectors;

public class IndexBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        List<List<Tuple<String, String>>> argNames = List.of("str", "list", "map").stream().map(s -> List.of(Tuple.of(s, "toIndex"), Tuple.of(s.equals("str") ? s : "var", "val"))).collect(Collectors.toList());
        argNames.forEach(list -> {
            List<Tuple<String, String>> additionalArgs = list;
            additionalArgs.add(Tuple.of("int", "startIndex"));
            argNames.add(additionalArgs);
        });
        return argNames;
    }

    @Override
    public String getName() {
        return "indexof";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return null;
    }
}
