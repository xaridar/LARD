package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;
import lscript.interpreting.types.Value;

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
