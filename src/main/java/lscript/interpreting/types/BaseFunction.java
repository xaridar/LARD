package lscript.interpreting.types;

import lscript.Constants;
import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.SymbolTable;

import java.util.List;

public class BaseFunction extends BasicType {

    protected String name;

    public BaseFunction(String name) {
        super("function");
        this.name = name;
    }

    public Context generateExecContext() {
        Context newContext = new Context(name, getContext(), getPosStart());
        newContext.setSymbolTable(new SymbolTable(getContext().getSymbolTable()));
        return newContext;
    }

    public RTResult checkArgs(List<Tuple<String, String>> argNames, List<Value> args) {
        RTResult res = new RTResult();

        if (args.size() > argNames.size())
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(),
                    String.format("%d too many args are passed into %s", args.size() - argNames.size(), name), getContext()));
        if (args.size() < argNames.size())
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(),
                    String.format("%d too few args are passed into %s", argNames.size() - args.size(), name), getContext()));

        return res.success(null);
    }

    public RTResult populateArgs(List<Tuple<String, String>> argNames, List<Value> args, Context execCtx) {
        RTResult res = new RTResult();

        for (int i = 0; i < args.size(); i++) {
            String argType = argNames.get(i).getLeft();
            String argName = argNames.get(i).getRight();
            Value argValue = args.get(i);
            if (Constants.getInstance().TYPES.get(argType) != null && !Constants.getInstance().TYPES.get(argValue.getType()).contains(argType))
                return res.failure(new Error.RunTimeError(argValue.getPosStart(), argValue.getPosEnd(),
                        String.format("Wrong type passed as argument; Expected %s, got %s", argType, argValue.getType()), getContext()));
            argValue.setContext(execCtx);
            String oldType = execCtx.getSymbolTable().set(argType, argName, argValue, false);
            if (oldType != null)
                return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), String.format("Wrong type; expected %s, got %s", oldType, argType), execCtx));
            oldType = execCtx.getSymbolTable().set("function", getName(), this, false);
            if (oldType != null)
                return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), String.format("Wrong type; expected %s, got %s", oldType, argType), execCtx));
        }
        return res.success(null);
    }

    public RTResult checkAndPopArgs(List<Tuple<String, String>> argNames, List<Value> args, Context execContext) {
        RTResult res = new RTResult();

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
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("<function %s>", getName());
    }

    @Override
    public Boolean equalTo(Value other) {
        if (!(other instanceof Function)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(name.equals(((Function) other).getName())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        return (Boolean) new Boolean(!equalTo(other).getValue()).setPos(getPosStart(), getPosEnd()).setContext(getContext());
    }
}
