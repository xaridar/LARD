package xaridar.lscript.interpreting.types;

import xaridar.lscript.parsing.nodes.FuncDefNode;
import xaridar.lscript.parsing.nodes.VarNode;

import java.util.List;

public class LClass extends BasicType {

    private final String name;
    private final List<LFunction> statMethods;
    private final List<FuncDefNode> methods;
    private final List<Value> staticVars;
    private final List<VarNode> fields;
    private final LFunction constructor;

    public LClass(String name, LFunction constructor, List<LFunction> statMethods, List<FuncDefNode> methods, List<Value> staticVars, List<VarNode> fields) {
        super("class");
        this.name = name;
        this.statMethods = statMethods;
        this.methods = methods;
        this.staticVars = staticVars;
        this.fields = fields;
        this.constructor = constructor;
    }

    @Override
    public String toString() {
        return String.format("<class %s>", name);
    }



    @Override
    public Value copy() {
        LClass cls = new LClass(name, constructor, statMethods, methods, staticVars, fields);
        cls.setContext(context);
        cls.setPos(posStart, posEnd);
        return cls;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LClass))
            return (LBoolean) new LBoolean(false).setContext(context).setPos(posStart, posEnd);
        return (LBoolean) new LBoolean(name.equals(((LClass) other).getName())).setContext(context).setPos(posStart, posEnd);
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        return (LBoolean) new LBoolean(!equalTo(other).isTrue()).setContext(context).setPos(posStart, posEnd);
    }

    public String getName() {
        return name;
    }

    public List<Value> getStaticVars() {
        return staticVars;
    }

    public List<FuncDefNode> getMethods() {
        return methods;
    }

    public List<VarNode> getFields() {
        return fields;
    }

    public List<LFunction> getStatMethods() {
        return statMethods;
    }

    public LFunction getConstructor() {
        return constructor;
    }
}
