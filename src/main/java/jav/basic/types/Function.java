package jav.basic.types;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Interpreter;
import jav.basic.nodes.Node;
import jav.basic.results.RTResult;

import java.util.List;

public class Function extends BaseFunction {

    private Node bodyNode;
    private List<Tuple<String, String>> argNames;

    public Function(String name, Node bodyNode, List<Tuple<String, String>> argNames) {
        super(name == null ? "<anonymous>" : name);
        this.bodyNode = bodyNode;
        this.argNames = argNames;
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
        if (retVal == null) {
            retVal = NullType.Void;
        }
        return res.success(retVal);
    }

    @Override
    public Function copy() {
        Function f = new Function(name, bodyNode, argNames);
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

}
