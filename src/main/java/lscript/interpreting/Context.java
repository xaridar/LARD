package lscript.interpreting;

import lscript.lexing.Position;

public class Context {
    Position parentEntryPos;
    Context parent;
    String displayName;
    SymbolTable symbolTable;
    public Context(String displayName, Context parent, Position parentEntryPos) {
        this.parentEntryPos = parentEntryPos;
        this.parent = parent;
        this.displayName = displayName;
        this.symbolTable = null;
    }

    public Context getParent() {
        return parent;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Position getParentEntryPos() {
        return parentEntryPos;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
