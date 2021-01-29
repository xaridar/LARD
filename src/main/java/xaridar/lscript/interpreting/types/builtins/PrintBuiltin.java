package xaridar.lscript.interpreting.types.builtins;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.NullType;
import xaridar.lscript.interpreting.types.Value;

import java.util.Collections;
import java.util.List;

public class PrintBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("var", "val")));
    }

    @Override
    public String getName() {
        return "print";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Value val = execCtx.getSymbolTable().get("val");
        if (val == NullType.Void) {
            return new RunTimeResult().failure(new Error.RunTimeError(fun.getPosStart(), fun.getPosEnd(), "Expected type; got void instead", execCtx));
        }
        String print = val.toString();
        System.out.println(print);
        return new RunTimeResult().success(NullType.Void);
    }
}
