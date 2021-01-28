package xaridar.lscript.interpreting;

import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.types.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A table of variable names and values for accessing.
 */
public class SymbolTable {
    List<Symbol> symbols;
    SymbolTable parent;

    /**
     * Default constructor.
     */
    public SymbolTable() {
        symbols = new ArrayList<>();
        parent = null;
    }

    /**
     * Overloaded constructor, which takes a parent SymbolTable as its only argument.
     * @param parent - a parent SymbolTable to be used for this one.
     */
    public SymbolTable(SymbolTable parent) {
        symbols = new ArrayList<>();
        this.parent = parent;
    }

    /**
     * @return This SymbolTable's stored list of Symbols.
     */
    public List<Symbol> getSymbols() {
        return symbols;
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
     * @param varName - The name of the variable or function to access.
     * @return The value of the stored variable with the provided name, if there is one. Returns null if a variable with the provided name cannot be found.
     */
    public Value get(String varName) {
        Symbol value = getSymbolByName(varName);
        if (value == null && parent != null) {
            return parent.get(varName);
        } else if (value != null) {
            return value.getValue();
        }
        return null;
    }

    /**
     * Checks against the current set of variables, and sets a variable based on type, name, value, and mutability.
     * @param type - A Token representing the type of the variable.
     * @param varName - The name of the variable to store or update.
     * @param value - The value to store with the variable name.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     * @return Null if the variable is stored successfully, or a String representing the expected type of the variable if it conflicts with the provided one.
     */
    public Error set(String type, String varName, Value value, ModifierList mods) {
        return set(type, varName, value, mods, false);
    }

    /**
     * Checks against the current set of variables, and sets a variable based on type, name, value, and mutability.
     * @param type - A Token representing the type of the variable.
     * @param varName - The name of the variable to store or update.
     * @param value - The value to store with the variable name.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     * @param ignoreRedefine - A boolean representing whether to ignore redefinition of variables.
     * @return Null if the variable is stored successfully, or a String representing the expected type of the variable if it conflicts with the provided one.
     */
    public Error set(String type, String varName, Value value, ModifierList mods, boolean ignoreRedefine) {
        Symbol symbol = getSymbolByName(varName);
        if (type == null && symbol == null) {
            symbol = getParentSymbolByName(varName);
        }
        if (symbol != null && symbol.canEdit()) {
            if (type == null || (ignoreRedefine && symbol.typeEquals(type))) {
                if (symbol.typeEquals(value.getType())) {
                    symbol.setValue(value);
                    return null;
                }
            } else {
                return new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Variable already defined: expected no variable type.", value.getContext());
            }
            return new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + symbol.getType() + "', got '" + value.getType() + "'", value.getContext());
        } else if (type != null) {
            if (mods.isStat()) {
                moveUp(type, varName, value, mods);
            }
            else {
                Symbol s = new Symbol(varName, type, value, mods.isFin(), mods.getPriv() == ModifierList.Privacy.PUBLIC, mods.isStat());
                symbols.add(s);
            }
        }
        return null;
    }

    /**
     * Removes a variable from the SymbolTable by name.
     * @param varName - The name of the variable to remove.
     */
    public void remove(String varName) {
        symbols.remove(getSymbolByName(varName));
    }

    /**
     * Removes a collection of variables from the SymbolTable by name.
     * @param names - A list of the names of the variables to remove.
     */
    public void removeAll(List<String> names) {
        symbols.removeAll(names.stream().map(this::getSymbolByName).collect(Collectors.toList()));
    }

    /**
     * Returns a boolean representing whether this SymbolTable has stored a variable with the given name.
     * @param varName - The name of the variable to search for.
     * @return True if the variable name is found.
     */
    public boolean hasVar(String varName) {
        return get(varName) != null;
    }

    /**
     * Moves through parent contexts until the base context is found, where a constant variable is saved.
     * @param type - A Token representing the type of the variable.
     * @param varName - The name of the variable to store or update.
     * @param value - The value to store with the variable name.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     */
    public void moveUp(String type, String varName, Value value, ModifierList mods) {
        if (parent != null)
            parent.moveUp(type, varName, value, mods);
        else {
            Symbol s = new Symbol(varName, type, value, mods.isFin(), mods.getPriv() == ModifierList.Privacy.PUBLIC, true);
            symbols.add(s);
        }
    }

    /**
     * Returns a Symbol from the List of Symbols via its name, or null if it does not exist.
     * @param varName - The name of the variable to find.
     * @return the first Symbol found in the list with the given name.
     */
    public Symbol getSymbolByName(String varName) {
        return symbols.stream().filter(symbol -> symbol.getName().equals(varName)).findFirst().orElse(null);
    }

    /**
     * Returns a Symbol from the List of Symbols via its name, checking all parents, or null if it does not exist.
     * @param varName - The name of the variable to find.
     * @return the first Symbol found in the list with the given name.
     */
    public Symbol getParentSymbolByName(String varName) {
        if (parent != null) {
            return parent.getParentSymbolByName(varName);
        }
        return symbols.stream().filter(symbol -> symbol.getName().equals(varName)).findFirst().orElse(null);
    }

    public void removeAll() {
        symbols.clear();
    }
}
