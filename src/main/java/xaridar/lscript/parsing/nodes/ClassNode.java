package xaridar.lscript.parsing.nodes;

import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.lexing.Position;
import xaridar.lscript.lexing.Token;

import java.util.List;

/**
 * Represents a class declaration.
 */
public class ClassNode extends VarNode {
    private final Token varName;
    private final List<VarNode> fields;
    private final List<FuncDefNode> methods;
    private final FuncDefNode constructor;

    /**
     * @param varName      - The name to assign to the class.
     * @param fields       - A list of the fields of the class.
     * @param methods      - A list of the class's methods.
     * @param constructor  - The constructor of the class.
     * @param posStart     - The start Position of the class definition.
     * @param mods         - A ModifierList containing all of the modifiers for the variable.
     */
    public ClassNode(Token varName, List<VarNode> fields, List<FuncDefNode> methods, FuncDefNode constructor, Position posStart, Position posEnd, ModifierList mods) {
        super(posStart, posEnd, mods, (String) varName.getValue());
        this.varName = varName;
        this.fields = fields;
        this.methods = methods;
        this.constructor = constructor;
    }

    public Token getVarName() {
        return varName;
    }

    public List<FuncDefNode> getMethods() {
        return methods;
    }

    public List<VarNode> getFields() {
        return fields;
    }

    public FuncDefNode getConstructor() {
        return constructor;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
