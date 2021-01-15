package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;
import jav.basic.types.Value;

import java.util.List;

public class PrintBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("var", "val")));
    }

    @Override
    public String getName() {
        return "print";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Value val = execCtx.getSymbolTable().get("val");
        if (val == NullType.Void) {
            return new RTResult().failure(new Error.RunTimeError(fun.getPosStart(), fun.getPosEnd(), "Expected type; got void instead", execCtx));
        }
        String print = val.toString();
        System.out.println(print);
        return new RTResult().success(NullType.Void);
    }
}
