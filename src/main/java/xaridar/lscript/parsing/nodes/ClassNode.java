package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

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
    private final VarAccessNode extendsNode;

    /**
     * @param varName      - The name to assign to the class.
     * @param fields       - A list of the fields of the class.
     * @param methods      - A list of the class's methods.
     * @param constructor  - The constructor of the class.
     * @param posStart     - The start Position of the class definition.
     * @param mods         - A ModifierList containing all of the modifiers for the variable.
     * @param extendsNode  - A Node representing the class for this class to extend, if any.
     */
    public ClassNode(Token varName, List<VarNode> fields, List<FuncDefNode> methods, FuncDefNode constructor, Position posStart, Position posEnd, ModifierList mods, VarAccessNode extendsNode) {
        super(posStart, posEnd, mods, (String) varName.getValue());
        this.varName = varName;
        this.fields = fields;
        this.methods = methods;
        this.constructor = constructor;
        this.extendsNode = extendsNode;
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

    public VarAccessNode getExtendNode() {
        return extendsNode;
    }
}
