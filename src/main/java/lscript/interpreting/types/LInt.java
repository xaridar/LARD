package lscript.interpreting.types;

public class LInt extends LNumber {

    private final int value;

    public LInt(int value) {
        super("int", value);
        this.value = value;
    }

    @Override
    public LInt copy() {
        LInt b = new LInt(value);
        b.setContext(getContext());
        b.setPos(getPosStart(), getPosEnd());
        return b;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
