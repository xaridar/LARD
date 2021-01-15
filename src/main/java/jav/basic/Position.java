package jav.basic;

public class Position {
    private int idx;
    private int ln;
    private int col;
    private String fn;
    private String ftxt;

    public Position(int idx, int ln, int col, String fn, String ftxt) {
        this.idx = idx;
        this.ln = ln;
        this.col = col;
        this.fn = fn;
        this.ftxt = ftxt;
    }

    public int getCol() {
        return col;
    }

    public int getIdx() {
        return idx;
    }

    public int getLn() {
        return ln;
    }

    public String getFn() {
        return fn;
    }

    public String getFtxt() {
        return ftxt;
    }

    public Position advance(Character current_char) {
        idx += 1;
        col += 1;

        if (current_char != null)
            if (current_char == '\n') {
                ln += 1;
                col = 0;
            }

        return this;
    }

    public Position copy() {
        return new Position(idx, ln, col, fn, ftxt);
    }
}
