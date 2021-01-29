package xaridar.lscript;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

/**
 * Holds constant Token types and variable types for the interpreter
 */
public enum TokenEnum {
    TT_INT,
    TT_FLOAT,
    TT_KW,
    TT_IDENTIFIER,
    TT_STR,
    TT_LEFT_BRACKET,
    TT_RIGHT_BRACKET,

    TT_PLUS,
    TT_MINUS,
    TT_MUL,
    TT_DIV,
    TT_MOD,
    TT_LEFT_PAREN,
    TT_RIGHT_PAREN,
    TT_EOF,
    TT_POW,
    TT_EQ,
    TT_COMMA,
    TT_DOT,
    TT_BANG,
    TT_SEMICOLON,
    TT_AND,
    TT_PIPE,
    TT_DOUBLE_PIPE,
    TT_QUESTION,
    TT_COLON,
    TT_LEFT_BRACE,
    TT_RIGHT_BRACE,

    TT_PLUSEQ,
    TT_MINUSEQ,
    TT_TIMESEQ,
    TT_DIVEQ,
    TT_MODEQ,
    TT_POWEQ,
    TT_LT,
    TT_GT,
    TT_BOOLEQ,
    TT_NEQ,
    TT_LEQ,
    TT_GEQ
}
