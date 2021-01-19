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
        if (retVal == null)
            retVal = NullType.Void;
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
