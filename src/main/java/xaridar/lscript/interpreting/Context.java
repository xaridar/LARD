package xaridar.lscript.interpreting;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Constants;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.types.LClass;
import xaridar.lscript.lexing.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A nested context of the program, which includes a SymbolTable holding all of the Context's variable, as well as a name for error generation.
 */
public class Context {
    Position parentEntryPos;
    Context parent;
    String displayName;
    SymbolTable symbolTable;
    Map<String, Context> accessibleContainedContexts;
    List<String> types;
    boolean isClass;

    /**
     * @param displayName - The name of the context, to be displayed on error stack traces.
     * @param parent - The parent Context, if there is one. Can be null.
     * @param parentEntryPos - The Position in the parent Context, if there is one, where this Context started. Can be null.
     * @param isClass - A boolean representing whether this context represents a class.
     */
    public Context(String displayName, Context parent, Position parentEntryPos, boolean isClass) {
        this.parentEntryPos = parentEntryPos;
        this.parent = parent;
        this.displayName = displayName;
        this.symbolTable = null;
        this.accessibleContainedContexts = new HashMap<>();
        types = new ArrayList<>(Constants.getInstance().TYPES.keySet());
        this.isClass = isClass;
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

    /**
     * Adds a contained Context to be accessible through this Context.
     * @param name - The name under which this Context should store the provided Context.
     * @param c - The Context to add.
     */
    public void addContainedContext(String name, Context c) {
        accessibleContainedContexts.put(name, c);
        c.parent = this;
    }

    /**
     * @param name - The Context name to search for.
     * @return The Context in this Context's list of contained Contexts that starts with the provided name, or null if it is not found.
     */
    public Context getContainedByName(String name) {
        return accessibleContainedContexts.keySet().stream().filter(str -> str.equals(name)).findFirst().map(str -> accessibleContainedContexts.get(str)).orElse(null);
    }

    /**
     * Adds a class to be accessible through this Context.
     * @param cls - The class to add.
     * @param c - The internal context of the class.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     * @return an Error if one occurs in saving the class.
     */
    public Error addClass(LClass cls, Context c, ModifierList mods) {
        Error err = getSymbolTable().set("class", cls.getName(), cls, mods);
        if (err != null) return err;
        accessibleContainedContexts.put(cls.getName(), c);
        types.add(cls.getName());
        return null;
    }

    public List<String> getTypes() {
        return types;
    }

    public boolean hasType(String type) {
        if (types.contains(type)) return true;
        if (parent != null) {
            return parent.hasType(type);
        }
        return false;
    }

    public void addType(String type) {
        types.add(type);
    }

    public Context getClassCtx() {
        if (isClass) return this;
        if (parent != null) return parent.getClassCtx();
        return null;
    }
}
