package lscript.interpreting;

import lscript.Constants;
import lscript.interpreting.types.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * A table of variable names and values for accessing.
 */
public class SymbolTable {
    Map<String, Map<String, Object>> symbols;
    SymbolTable parent;

    /**
     * Default constructor.
     */
    public SymbolTable() {
        symbols = new HashMap<>();
        parent = null;
    }

    /**
     * Overloaded constructor, which takes a parent SymbolTable as its only argument.
     * @param parent - a parent SymbolTable to be used for this one.
     */
    public SymbolTable(SymbolTable parent) {
        symbols = new HashMap<>();
        this.parent = parent;
    }

    /**
     * @param parent - A new SymbolTable to set as the parent of this one.
     */
    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * @return This SymbolTable's parent.
     */
    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Accesses a variable in the Map by name, if it exists.
     * @param var_name - The name of the variable or function to access.
     * @return The value of the stored variable with the provided name, if there is one. Returns null if a variable with the provided name cannot be found.
     */
    public Value get(String var_name) {
        Map<String, Object> value = symbols.getOrDefault(var_name, null);
        if (value == null && parent != null) {
            return parent.get(var_name);
        } else if (value != null) {
            return (Value) value.get("value");
        }
        return null;
    }

    /**
     * Checks against the current set of variables, and sets a variable based on type, name, value, and mutability.
     * @param type - A String representing the type of the variable.
     * @param var_name - The name of the variable to store or update.
     * @param value - The value to store with the variable name.
     * @param immutable - A boolean representing whether the variable can be changed in the future.
     * @return Null if the variable is stored successfully, or a String representing the expected type of the variable if it conflicts with the provided one.
     */
    public String set(String type, String var_name, Value value, boolean immutable) {
        if (symbols.containsKey(var_name) && !(boolean) symbols.get(var_name).get("immutable")) {
            if (type == null) {
                if (value.getType().equals(symbols.get(var_name).get("type"))) {
                    symbols.get(var_name).put("value", value);
                    return null;
                }
            } else if (symbols.get(var_name).get("type").equals(type) || Constants.getInstance().TYPES.get(type) == null) {
                symbols.get(var_name).put("value", value);
                return null;
            }
            return (String) symbols.get(var_name).get("type");
        } else if (type != null) {
            if (type.equals("const"))
                moveUp(type, var_name, value);
            else {
                Map<String, Object> val = new HashMap<>();
                val.put("type", type);
                val.put("value", value);
                val.put("immutable", immutable);
                symbols.put(var_name, val);
            }
        }
        return null;
    }

    /**
     * Removes a variable fromm the SymbolTable by name.
     * @param var_name - The name of the variable to remove.
     */
    public void remove(String var_name) {
        symbols.remove(var_name);
    }

    /**
     * Returns a boolean representing whether this SymbolTable has stored a variable with the given name.
     * @param var_name - The name of the variable to search for.
     * @return True if the variable name is found.
     */
    public boolean hasVar(String var_name) {
        return symbols.containsKey(var_name);
    }

    public void moveUp(String type, String var_name, Object value) {
        if (parent != null)
            parent.moveUp(type, var_name, value);
        else {
            Map<String, Object> val = new HashMap<>();
            val.put("type", type);
            val.put("value", value);
            val.put("immutable", true);

            symbols.put(var_name, val);
        }
    }
}
