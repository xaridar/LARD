package lscript;

import lscript.interpreting.types.BasicType;
import lscript.interpreting.types.LByte;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.LString;

import java.util.*;

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
        TYPES.put("int", Arrays.asList("int", "float", "num"));
        TYPES.put("float", Arrays.asList("float", "num"));
        TYPES.put("var", null);
        TYPES.put("const", null);
        TYPES.put("bool", Collections.singletonList("bool"));
        TYPES.put("function", Collections.singletonList("function"));
        TYPES.put("void", Collections.emptyList());
        TYPES.put("str", Collections.singletonList("str"));
        TYPES.put("nullType", Collections.emptyList());
        TYPES.put("list", Collections.singletonList("list"));
        TYPES.put("map", Collections.singletonList("map"));
        TYPES.put("num", Collections.singletonList("float"));
        TYPES.put("file", Collections.singletonList("file"));
        TYPES.put("byte", Arrays.asList("int", "float", "num"));

        CONVERT_CLASSES = new HashMap<>();
        CONVERT_CLASSES.put("str", LString.class);
        CONVERT_CLASSES.put("list", LList.class);
        CONVERT_CLASSES.put("byte", LByte.class);

        DESIGNATED_KEYWORDS = new ArrayList<>();
        DESIGNATED_KEYWORDS.addAll(TYPES.keySet());
        DESIGNATED_KEYWORDS.addAll(Arrays.asList("if", "elif", "else", "while", "for", "func", "return", "continue", "break", "import", "from", "as"));

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

        EQUAL_MODS.put(Character.valueOf('+'), plusMod);
        EQUAL_MODS.put(Character.valueOf('-'), minusMod);
        EQUAL_MODS.put(Character.valueOf('*'), mulMod);
        EQUAL_MODS.put(Character.valueOf('/'), divMod);
        EQUAL_MODS.put(Character.valueOf('^'), powMod);
        EQUAL_MODS.put(Character.valueOf('%'), modMod);
    }
}
