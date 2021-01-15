package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.*;
import jav.basic.types.Float;

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
