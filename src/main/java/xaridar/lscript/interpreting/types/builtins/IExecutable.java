package xaridar.lscript.interpreting.types.builtins;

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
import xaridar.lscript.interpreting.types.builtins.files.OpenBuiltin;
import xaridar.lscript.interpreting.types.builtins.files.ReadFileBultin;
import xaridar.lscript.interpreting.types.builtins.files.WriteFileBuiltin;
import xaridar.lscript.interpreting.types.builtins.input.InputBuiltin;
import xaridar.lscript.interpreting.types.builtins.input.NumInputBuiltin;
import xaridar.lscript.interpreting.types.builtins.lists.*;
import xaridar.lscript.interpreting.types.builtins.math.*;
import xaridar.lscript.interpreting.types.builtins.random.RandBuiltin;
import xaridar.lscript.interpreting.types.builtins.random.RandIntBuiltin;

import java.util.Arrays;
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
    RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun);

    List<IExecutable> builtins = Arrays.asList(
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
            new GetBytesBuiltin(),

            new RandBuiltin(),
            new RandIntBuiltin()
    );
}
