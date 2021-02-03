package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.lexing.Position;

public class VarNode extends Node {
    private final ModifierList mods;
    private final String name;

    /**
     * @param posStart - The start position of the node.
     * @param posEnd   - The end position of the node.
     * @param mods     - The modifiers to the variable.
     * @param name     - The name of the variable.
     */
    public VarNode(Position posStart, Position posEnd, ModifierList mods, String name) {
        super(posStart, posEnd);
        this.mods = mods;
        this.name = name;
    }

    public ModifierList getMods() {
        return mods;
    }

    public String getName() {
        return name;
    }
}
