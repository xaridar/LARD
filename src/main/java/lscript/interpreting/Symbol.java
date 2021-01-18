package lscript.interpreting;

import lscript.Constants;
import lscript.interpreting.types.Value;

/**
 * A data type that represents a variable in a SymbolTable.
 */
public class Symbol {

    private final String name;
    private final String type;
    private Value value;
    private final boolean immutable;

    /**
     * Default constructor for Symbol.
     * @param name - The name to be given to the variable.
     * @param type - The type of the variable stored.
     * @param value - The Value stored in the variable.
     * @param immutable - A boolean representing whether the variable's value can be changed.
     */
    public Symbol(String name, String type, Value value, boolean immutable) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.immutable = immutable;
    }

    /**
     * Overloaded constructor, which sets immutable to false.
     * @param name - The name to be given to the variable.
     * @param type - The type of the variable stored.
     * @param value - The Value stored in the variable.
     */
    public Symbol(String name, String type, Value value) {
        this(name, type, value, false);
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
        return this.type.equals(otherType) || Constants.getInstance().TYPES.get(otherType) == null;
    }

    /**
     * Returns a boolean representing whether this Symbol can be edited.
     * @return True if the Symbol can be edited; otherwise, false.
     */
    public boolean canEdit() {
        return !immutable;
    }
}
