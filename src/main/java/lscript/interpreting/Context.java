package lscript.interpreting;

import lscript.lexing.Position;

/**
 * A nested context of the program, which includes a SymbolTable holding all of the Context's variable, as well as a name for error generation.
 */
public class Context {
    Position parentEntryPos;
    Context parent;
    String displayName;
    SymbolTable symbolTable;

    /**
     * @param displayName - The name of the context, to be displayed on error stack traces.
     * @param parent - The parent Context, if there is one. Can be null.
     * @param parentEntryPos - The Position in the parent Context, if there is one, where this Context started. Can be null.
     */
    public Context(String displayName, Context parent, Position parentEntryPos) {
        this.parentEntryPos = parentEntryPos;
        this.parent = parent;
        this.displayName = displayName;
        this.symbolTable = null;
    }

    /**
     * @return The parent Context, if there is one. Can be null.
     */
    public Context getParent() {
        return parent;
    }

    /**
     * @return The Context's stored SymbolTable, which holds all variables and functions for the Context.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * @return The name of the context, to be displayed on error stack traces.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return The Position in the parent Context, if there is one, where this Context started. Can be null.
     */
    public Position getParentEntryPos() {
        return parentEntryPos;
    }

    /**
     * @param symbolTable - A SymbolTable to set to this Context.
     */
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
