package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.Float;

import java.util.List;
import java.util.Scanner;

public class NumInputBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(), List.of(Tuple.of("str", "prompt")));
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
            return new RTResult().success(new Float(java.lang.Float.parseFloat(s)));
        } else if (s.matches("^-?\\d+$")) {
            return new RTResult().success(new Int(Integer.parseInt(s)));
        }
        return new RTResult().success(NullType.Null);
    }
}
