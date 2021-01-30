package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Constants;
import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.SymbolTable;

import java.util.List;

public class BaseFunction extends BasicType {

    protected String name;

    public BaseFunction(String name) {
        super("function");
        this.name = name;
    }

    public Context generateExecContext(Context context) {
        Context newContext = new Context(name, context, getPosStart());
        newContext.setSymbolTable(new SymbolTable(context.getSymbolTable()));
        return newContext;
    }

    public Context generateExecContext() {
        Context newContext = new Context(name, getContext(), getPosStart());
        newContext.setSymbolTable(new SymbolTable(getContext().getSymbolTable()));
        return newContext;
    }

    public RunTimeResult checkArgs(List<Tuple<String, String>> argNames, List<Value> args) {
        RunTimeResult res = new RunTimeResult();

        if (args.size() > argNames.size())
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(),
                    String.format("%d too many args are passed into %s", args.size() - argNames.size(), name), getContext()));
        if (args.size() < argNames.size())
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(),
                    String.format("%d too few args are passed into %s", argNames.size() - args.size(), name), getContext()));

        return res.success(null);
    }

    public RunTimeResult populateArgs(List<Tuple<String, String>> argNames, List<Value> args, Context execCtx) {
        RunTimeResult res = new RunTimeResult();

        for (int i = 0; i < args.size(); i++) {
            String argType = argNames.get(i).getLeft();
            String argName = argNames.get(i).getRight();
            Value argValue = args.get(i);
            if (Constants.getInstance().TYPES.get(argType) != null && !Constants.getInstance().TYPES.get(argValue.getType()).contains(argType))
                return res.failure(new Error.ArgumentError(argValue.getPosStart(), argValue.getPosEnd(),
                        String.format("Wrong type passed as argument; Expected %s, got %s", argType, argValue.getType()), getContext()));
            argValue.setContext(execCtx);
            Error err = execCtx.getSymbolTable().set(argType, argName, argValue, ModifierList.getDefault());
            if (err != null)
                return res.failure(err);
        }
        return res.success(null);
    }

    public RunTimeResult checkAndPopArgs(List<Tuple<String, String>> argNames, List<Value> args, Context execContext) {
        RunTimeResult res = new RunTimeResult();

        res.register(checkArgs(argNames, args));
        if (res.shouldReturn()) return res;

        res.register(populateArgs(argNames, args, execContext));
        if (res.shouldReturn()) return res;

        return res.success(null);
    }

    public String getName() {
        return name;
    }

    @Override
    public Value copy() {
        return new BaseFunction(name).setPos(getPosStart(), getPosEnd()).setContext(getContext());
    }

    @Override
    public String toString() {
        return String.format("<function %s>", getName());
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LFunction)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(name.equals(((LFunction) other).getName())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        return (LBoolean) new LBoolean(!equalTo(other).getValue()).setPos(getPosStart(), getPosEnd()).setContext(getContext());
    }
}
