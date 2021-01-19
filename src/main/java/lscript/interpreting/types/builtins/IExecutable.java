package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.builtins.files.OpenBuiltin;
import lscript.interpreting.types.builtins.files.ReadFileBultin;
import lscript.interpreting.types.builtins.files.WriteFileBuiltin;
import lscript.interpreting.types.builtins.input.InputBuiltin;
import lscript.interpreting.types.builtins.input.NumInputBuiltin;
import lscript.interpreting.types.builtins.lists.*;
import lscript.interpreting.types.builtins.math.*;
import lscript.interpreting.types.builtins.random.RandBuiltin;
import lscript.interpreting.types.builtins.random.RandIntBuiltin;

import java.util.List;

/**
 * Represents a language-builtin function.
 */
public interface IExecutable {
    /**
     * Returns all possible Sets of argument types and names for the function.
     * @return a List containing all possible Sets of argument types and names for the function.
     */
    List<List<Tuple<String, String>>> getArgNames();

    /**
     * @return The function's name.
     */
    String getName();

    /**
     * Runs the builtin function.
     * @param execCtx - The new Context for the function to run in.
     * @param execNum - The index in the argument list that matches the parameters passed.
     * @param fun - The BuiltinFunction value being called, for access to its state and variables.
     * @return
     */
    RTResult execute(Context execCtx, int execNum, BuiltInFunction fun);

    List<IExecutable> builtins = List.of(
            new PrintBuiltin(),
            new ClsBuiltin(),
            new QuitBuiltin(),
            new EvalBuiltin(),

            new InputBuiltin(),
            new NumInputBuiltin(),
            new TypeofBuiltin(),
            new StrBuiltin(),
            new FormatBuiltin(),
            new SplitBuiltin(),

            new RemoveBuiltin(),
            new AppendBuiltin(),
            new PopBuiltin(),
            new LenBuiltin(),
            new ContainsBuiltin(),
            new IndexBuiltin(),
            new LastIndexBuiltin(),
            new JoinBuiltin(),

            new SqrtBuiltin(),
            new RootBuiltin(),
            new CeilBuiltin(),
            new FloorBuiltin(),
            new RoundBuiltin(),
            new AbsBuiltin(),
            new MinBuiltin(),
            new MaxBuiltin(),

            new OpenBuiltin(),
            new ReadFileBultin(),
            new WriteFileBuiltin(),

            new RandBuiltin(),
            new RandIntBuiltin()
    );
}
