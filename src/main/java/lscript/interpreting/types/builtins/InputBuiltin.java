package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LString;

import java.util.List;
import java.util.Scanner;

public class InputBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(), List.of(Tuple.of("str", "prompt")));
    }

    @Override
    public String getName() {
        return "input";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 1) {
            System.out.print(execCtx.getSymbolTable().get("prompt"));
        }
        Scanner scanner = new Scanner(System.in);
        return new RTResult().success(new LString(scanner.nextLine()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
