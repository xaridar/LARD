package lscript.parsing.nodes;

import lscript.interpreting.ModifierList;
import lscript.lexing.Position;

public class VarNode extends Node {
    private final ModifierList mods;

    /**
     * @param posStart - The start position of the node.
     * @param posEnd   - The end position of the node.
     * @param mods     - The modifiers to the variable.
     */
    public VarNode(Position posStart, Position posEnd, ModifierList mods, String name) {
        super(posStart, posEnd);
        this.mods = mods;
    }

    public ModifierList getMods() {
        return mods;
    }
}
