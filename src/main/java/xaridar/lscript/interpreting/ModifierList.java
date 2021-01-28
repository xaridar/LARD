package lscript.interpreting;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lscript.errors.Error;

import java.util.ArrayList;
import java.util.List;

public class ModifierList {
    private Privacy priv;
    private Boolean stat;
    private Boolean fin;

    public ModifierList() {
        priv = null;
        stat = null;
        fin = null;
    }

    public static ModifierList getDefault() {
        ModifierList mods = new ModifierList();
        mods.setToDefaults();
        return mods;
    }

    public void setToDefaults() {
        if (priv == null) priv = Privacy.PUBLIC;
        if (stat == null) stat = false;
        if (fin == null) fin = false;
    }

    public Privacy getPriv() {
        return priv;
    }

    public boolean isFin() {
        return fin;
    }

    public boolean isStat() {
        return stat;
    }

    public interface Modifier {}

    public enum Privacy implements Modifier { PUBLIC, PRIVATE }

    public String addModByStringHarsh(String str) {
        if (str.equals("pub") || str.equals("priv")) {
            if (priv == null) {
                priv = str.equals("pub") ? Privacy.PUBLIC : Privacy.PRIVATE;
                return null;
            }
            return "Cannot add more than one privacy modifier to a value";
        }
        if (str.equals("stat")) {
            if (stat == null) {
                stat = true;
                return null;
            }
            return "Cannot use more than one static modifier";
        }
        if (str.equals("fin")) {
            if (fin == null) {
                fin = true;
                return null;
            }
            return "Cannot use more than one final modifier";
        }
        return "Modifier not found: '" + str + "'";
    }

    public String addModByString(String str) {
        String s = addModByStringHarsh(str);
        if (s != null && s.startsWith("Modifier not found")) return "";
        return s;
    }
}
