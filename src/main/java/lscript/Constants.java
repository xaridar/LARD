package lscript;

import lscript.interpreting.types.BasicType;
import lscript.interpreting.types.LList;
import lscript.interpreting.types.LString;

import lscript.TokenEnum.*;
import lscript.lexing.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds constant Token types and variable types for the interpreter
 */
public class Constants {
    public static final String TT_INT = "INT";
    public static final String TT_FLOAT = "FLOAT";
    public static final String TT_KW = "KW";
    public static final String TT_IDENTIFIER = "ID";
    public static final String TT_STR = "STR";
    public static final String TT_LEFT_BRACKET = "LEFT_BRCKT";
    public static final String TT_RIGHT_BRACKET = "RIGHT_BRCKT";

    public Map<String, List<String>> TYPES;
    public Map<String, Class<? extends BasicType>> CONVERT_CLASSES;

    public List<String> DESIGNATED_KEYWORDS;

    public static final String TT_PLUS = "PLUS";
    public static final String TT_MINUS = "MINUS";
    public static final String TT_MUL = "MUL";
    public static final String TT_DIV = "DIV";
    public static final String TT_MOD = "MODULO";
    public static final String TT_LEFT_PAREN = "LEFT_PAREN";
    public static final String TT_RIGHT_PAREN = "RIGHT_PAREN";
    public static final String TT_EOF = "EOF";
    public static final String TT_POW = "POW";
    public static final String TT_EQ = "EQ";
    public static final String TT_COMMA = "COMMA";
    public static final String TT_BANG = "BANG";
    public static final String TT_SEMICOLON = "SEMICOLON";
    public static final String TT_AND = "AND";
    public static final String TT_PIPE = "PIPE";
    public static final String TT_DOUBLE_PIPE = "DBL_PIPE";
    public static final String TT_QUESTION = "QUESMARK";
    public static final String TT_COLON = "COLON";
    public static final String TT_LEFT_BRACE = "LEFT_BRACE";
    public static final String TT_RIGHT_BRACE = "RIGHT_BRACE";

    public static final String TT_PLUSEQ = "PLUSEQ";
    public static final String TT_MINUSEQ = "MINUSEQ";
    public static final String TT_TIMESEQ = "TIMESEQ";
    public static final String TT_DIVEQ = "DIVEQ";
    public static final String TT_MODEQ = "MODEQ";
    public static final String TT_POWEQ = "POWEQ";
    public Map<Character, Map<String, TokenEnum>> EQUAL_MODS;

    public static final String TT_LT = "LESS_THAN";
    public static final String TT_GT = "GREATER_THAN";
    public static final String TT_BOOLEQ = "BOOLEQ";
    public static final String TT_NEQ = "NEQ";
    public static final String TT_LEQ = "LESS_THAN_OR_EQ";
    public static final String TT_GEQ = "GREATER_THAN_OR_EQ";

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
        DESIGNATED_KEYWORDS.addAll(List.of("if", "elif", "else", "while", "for", "func", "switch", "case", "default", "return", "continue", "break"));

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
