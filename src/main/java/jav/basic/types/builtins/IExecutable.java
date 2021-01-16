package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.builtins.files.OpenBuiltin;
import jav.basic.types.builtins.files.ReadFileBultin;
import jav.basic.types.builtins.files.WriteFileBuiltin;
import jav.basic.types.builtins.math.*;

import java.util.List;

public interface IExecutable {
    List<List<Tuple<String, String>>> getArgNames();
    String getName();
    RTResult execute(Context execCtx, int execNum, BuiltInFunction fun);

    List<IExecutable> builtins = List.of(
            new PrintBuiltin(),
            new InputBuiltin(),
            new TypeofBuiltin(),
            new StrBuiltin(),
            new RemoveBuiltin(),
            new AppendBuiltin(),
            new ClsBuiltin(),
            new PopBuiltin(),
            new SqrtBuiltin(),
            new RootBuiltin(),
            new CeilBuiltin(),
            new FloorBuiltin(),
            new RoundBuiltin(),
            new AbsBuiltin(),
            new NumInputBuiltin(),
            new MinBuiltin(),
            new MaxBuiltin(),
            new LenBuiltin(),
            new FormatBuiltin(),
            new OpenBuiltin(),
            new ReadFileBultin(),
            new WriteFileBuiltin(),
            new EvalBuiltin(),
            new QuitBuiltin(),
            new ContainsBuiltin(),
            new IndexBuiltin(),
            new LastIndexBuiltin()
    );
}
