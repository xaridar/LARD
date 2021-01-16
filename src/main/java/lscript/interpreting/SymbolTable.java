package lscript.interpreting;

import lscript.Constants;
import lscript.interpreting.types.Value;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Map<String, Object>> symbols;
    SymbolTable parent;

    public SymbolTable() {
        symbols = new HashMap<>();
        parent = null;
    }

    public SymbolTable(SymbolTable parent) {
        symbols = new HashMap<>();
        this.parent = parent;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public Value get(String var_name) {
        Map<String, Object> value = symbols.getOrDefault(var_name, null);
        if (value == null && parent != null) {
            return parent.get(var_name);
        } else if (value != null) {
            return (Value) value.get("value");
        }
        return null;
    }

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

    public void remove(String var_name) {
        symbols.remove(var_name);
    }

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
