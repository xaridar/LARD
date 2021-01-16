package lscript.interpreting.types;

import lscript.Constants;
import lscript.*;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.Interpreter;
import lscript.parsing.nodes.Node;
import lscript.interpreting.RTResult;

import java.util.ArrayList;
import java.util.List;

public class LFunction extends BaseFunction {

    private final Node bodyNode;
    private final List<Tuple<String, String>> argNames;
    private final List<String> retTypes;

    public LFunction(String name, Node bodyNode, List<Tuple<String, String>> argNames, List<String> retTypes) {
        super(name == null ? "<anonymous>" : name);
        this.bodyNode = bodyNode;
        this.argNames = argNames;
        if (retTypes.size() == 1 && retTypes.get(0).equals("void")) {
            retTypes = new ArrayList<>();
        }
        this.retTypes = retTypes;
    }

    @Override
    public RTResult execute(List<Value> args) {
        RTResult res = new RTResult();
        Interpreter interpreter = Interpreter.getInstance();
        Context newContext = generateExecContext();

        res.register(checkAndPopArgs(argNames, args, newContext));
        if (res.shouldReturn()) return res;

        res.register(interpreter.visit(bodyNode, newContext));
        if (res.getFuncRetVal() == null && res.shouldReturn()) return res;
        Value retVal = res.getFuncRetVal();
        if (retVal instanceof LList) {
            if (retTypes.size() != ((LList) retVal).getElements().size()) {
                return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "Wrong number of return types; Expected " + retTypes.size() + ", got " + ((LList) retVal).getElements().size(), newContext));
            }
            for (int i = 0, retValSize = ((LList) retVal).getElements().size(); i < retValSize; i++) {
                Value val = ((LList) retVal).getElements().get(i);
                if (!(Constants.getInstance().TYPES.get(retTypes.get(i)) == null || Constants.getInstance().TYPES.get(val.getType()).contains(retTypes.get(i)))) {
                    return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "Wrong type; Expected '" + retTypes.get(i) + "', got '" + val.getType() + "'", newContext));
                }
            }
        } else if (retVal != null) {
            if (retTypes.size() != 1) {
                return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "Wrong number of return types; Expected " + retTypes.size() + ", got 1", newContext));
            }
        }else {
            if (retTypes.size() != 0) {
                return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "Wrong number of return types; Expected " + retTypes.size() + ", got 0", newContext));
            }
            retVal = NullType.Void;
        }
        return res.success(retVal);
    }

    @Override
    public LFunction copy() {
        LFunction f = new LFunction(name, bodyNode, argNames, retTypes);
        f.setContext(getContext());
        f.setPos(getPosStart(), getPosEnd());
        return f;
    }

    public List<Tuple<String, String>> getArgNames() {
        return argNames;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    public List<String> getRetTypes() {
        return retTypes;
    }
}
