package xaridar.lscript.lexing;

/**
 * A datatype that stored a position in the input String.
 */
public class Position {
    private int idx;
    private int ln;
    private int col;
    private final String fn;
    private final String ftxt;

    /**
     * @param idx - The index of the Position in the String.
     * @param ln - The line number where the Position points to.
     * @param col - The column number where the Position points to.
     * @param fn - The file name of the text where the Position points.
     * @param ftxt - The full text of the file.
     */
    public Position(int idx, int ln, int col, String fn, String ftxt) {
        this.idx = idx;
        this.ln = ln;
        this.col = col;
        this.fn = fn;
        this.ftxt = ftxt;
    }

    /**
     * @return The column number where the Position points to.
     */
    public int getCol() {
        return col;
    }

    /**
     * @return The index of the Position in the String.
     */
    public int getIdx() {
        return idx;
    }

    /**
     * @return The line number where the Position points to.
     */
    public int getLn() {
        return ln;
    }

    /**
     * @return The file name of the text where the Position points.
     */
    public String getFn() {
        return fn;
    }

    /**
     * @return The full text of the file.
     */
    public String getFtxt() {
        return ftxt;
    }

    /**
     * Advances the Position and returns itself.
     * @param currentChar - The current character of the Lexer, passed in to determine whether a new line has been reached.
     * @return this Position.
     */
    public Position advance(Character currentChar) {
        idx += 1;
        col += 1;

        if (currentChar != null)
            if (currentChar == '\n') {
                ln += 1;
                col = 0;
            }

        return this;
    }

    /**
     * Copies the Position into a new Position.
     * @return The copied Position.
     */
    public Position copy() {
        return new Position(idx, ln, col, fn, ftxt);
    }
}
