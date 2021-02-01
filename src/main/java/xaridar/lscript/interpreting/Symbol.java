package xaridar.lscript.interpreting;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Constants;
import xaridar.lscript.interpreting.types.Value;

/**
 * A data type that represents a variable in a SymbolTable.
 */
public class Symbol {

    private final String name;
    private final String type;
    private Value value;
    private boolean immutable;
    private final boolean accessible;
    private final boolean stat;
    private final Context context;

    /**
     * Default constructor for Symbol.
     * @param name - The name to be given to the variable.
     * @param type - The type of the variable stored.
     * @param value - The Value stored in the variable.
     * @param immutable - A boolean representing whether the variable's value can be changed.
     * @param accessible - A boolean representing whether the variable's value can be accessed from other contexts.
     * @param stat - A boolean representing whether the variable's value is static.
     * @param context - This Symbol's Context.
     */
    public Symbol(String name, String type, Value value, boolean immutable, boolean accessible, boolean stat, Context context) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.immutable = immutable;
        this.accessible = accessible;
        this.stat = stat;
        this.context = context;
    }

    /**
     * Overloaded constructor, which sets immutable to false.
     * @param name - The name to be given to the variable.
     * @param type - The type of the variable stored.
     * @param value - The Value stored in the variable.
     * @param context - This Symbol's Context.
     */
    public Symbol(String name, String type, Value value, Context context) {
        this(name, type, value, false, true, false, context);
    }

    /**
     * @return The variable's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The Value stored in the variable.
     */
    public Value getValue() {
        return value;
    }

    /**
     * @param value - The Value to be stored in the Symbol.
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * @return The type of the variable stored.
     */
    public String getType() {
        return type;
    }

    /**
     * Determines whether the type of this Symbol matches a provided type.
     * @param otherType - A String containing the type of variable to check against.
     * @return True if the types match; otherwise, false.
     */
    public boolean typeEquals(String otherType) {
        return this.type.equals(otherType) || otherType.equals("nullType") || Constants.getInstance().TYPES.get(this.type) == null;
    }

    /**
     * Returns a boolean representing whether this Symbol can be edited.
     * @return True if the Symbol can be edited; otherwise, false.
     */
    public boolean canEdit() {
        return !immutable;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public boolean isStatic() {
        return stat;
    }

    public Context getContext() {
        return context;
    }

    public void setImmutable() {
        immutable = true;
    }
}
