package lscript.interpreting.types.builtins.input;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.LFloat;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class NumInputBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.emptyList(), Collections.singletonList(Tuple.of("str", "prompt")));
    }

    @Override
    public String getName() {
        return "numinput";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 1) {
            System.out.print(execCtx.getSymbolTable().get("prompt"));
        }
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        if (s.matches("^-?\\d+\\.\\d+$")) {
            return new RTResult().success(new LFloat(java.lang.Float.parseFloat(s)).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        } else if (s.matches("^-?\\d+$")) {
            return new RTResult().success(new LInt(Integer.parseInt(s)).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        }
        return new RTResult().success(NullType.Null);
    }
}
