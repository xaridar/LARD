package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.Interpreter;
import xaridar.lscript.parsing.nodes.Node;
import xaridar.lscript.interpreting.RunTimeResult;

import java.util.ArrayList;
import java.util.List;
import xaridar.lscript.Tuple;

public class LFunction extends BaseFunction {

    private Node bodyNode;
    private final List<Tuple<String, String>> argNames;
    private List<String> retTypes;

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
    public RunTimeResult execute(List<Value> args) {
        RunTimeResult res = new RunTimeResult();
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
