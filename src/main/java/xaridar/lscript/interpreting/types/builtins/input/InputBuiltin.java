package xaridar.lscript.interpreting.types.builtins.input;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LString;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class InputBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.emptyList(), Collections.singletonList(Tuple.of("str", "prompt")));
    }

    @Override
    public String getName() {
        return "input";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 1) {
            System.out.print(execCtx.getSymbolTable().get("prompt"));
        }
        Scanner scanner = new Scanner(System.in);
        return new RunTimeResult().success(new LString(scanner.nextLine()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
