package lscript;

import lscript.interpreting.types.BasicType;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.LString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds constant Maps for ease of parsing and interpreting
 */
public class Constants {

    public Map<String, List<String>> TYPES;
    public Map<String, Class<? extends BasicType>> CONVERT_CLASSES;

    public List<String> DESIGNATED_KEYWORDS;
    public Map<Character, Map<String, TokenEnum>> EQUAL_MODS;

    private static Constants INSTANCE;

    public static Constants getInstance() {
        if (INSTANCE == null) INSTANCE = new Constants();
        return INSTANCE;
    }

    /**
     * Sets up maps in the singleton
     */
    private Constants() {
        TYPES = new HashMap<>();
        TYPES.put("int", List.of("int", "float", "num"));
        TYPES.put("float", List.of("float", "num"));
        TYPES.put("var", null);
        TYPES.put("const", null);
        TYPES.put("bool", List.of("bool"));
        TYPES.put("function", List.of("function"));
        TYPES.put("void", List.of());
        TYPES.put("str", List.of("str"));
        TYPES.put("nullType", List.of());
        TYPES.put("list", List.of("list"));
        TYPES.put("map", List.of("map"));
        TYPES.put("num", List.of("float"));
        TYPES.put("file", List.of("file"));

        CONVERT_CLASSES = new HashMap<>();
        CONVERT_CLASSES.put("str", LString.class);
        CONVERT_CLASSES.put("list", LList.class);

        DESIGNATED_KEYWORDS = new ArrayList<>();
        DESIGNATED_KEYWORDS.addAll(TYPES.keySet());
        DESIGNATED_KEYWORDS.addAll(List.of("if", "elif", "else", "while", "for", "func", "return", "continue", "break", "import", "from", "as"));

        EQUAL_MODS = new HashMap<>();
        HashMap<String, TokenEnum> plusMod = new HashMap<>();

        plusMod.put("with", TokenEnum.TT_PLUSEQ);
        plusMod.put("without", TokenEnum.TT_PLUS);

        HashMap<String, TokenEnum> minusMod = new HashMap<>();

        minusMod.put("with", TokenEnum.TT_MINUSEQ);
        minusMod.put("without", TokenEnum.TT_MINUS);

        HashMap<String, TokenEnum> mulMod = new HashMap<>();

        mulMod.put("with", TokenEnum.TT_TIMESEQ);
        mulMod.put("without", TokenEnum.TT_MUL);

        HashMap<String, TokenEnum> divMod = new HashMap<>();

        divMod.put("with", TokenEnum.TT_DIVEQ);
        divMod.put("without", TokenEnum.TT_DIV);

        HashMap<String, TokenEnum> powMod = new HashMap<>();

        powMod.put("with", TokenEnum.TT_POWEQ);
        powMod.put("without", TokenEnum.TT_POW);

        HashMap<String, TokenEnum> modMod = new HashMap<>();

        modMod.put("with", TokenEnum.TT_MODEQ);
        modMod.put("without", TokenEnum.TT_MOD);

        EQUAL_MODS.put('+', plusMod);
        EQUAL_MODS.put('-', minusMod);
        EQUAL_MODS.put('*', mulMod);
        EQUAL_MODS.put('/', divMod);
        EQUAL_MODS.put('^', powMod);
        EQUAL_MODS.put('%', modMod);
    }
}
