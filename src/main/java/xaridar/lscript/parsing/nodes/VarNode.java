package xaridar.lscript.parsing.nodes;

import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.lexing.Position;

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
