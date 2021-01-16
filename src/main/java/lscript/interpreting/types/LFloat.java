package lscript.interpreting.types;

public class LFloat extends LNumber {

    private final float value;

    public LFloat(float value) {
        super("float", value);
        this.value = value;
    }

    public LFloat(int value) {
        super("float", value);
        this.value = value;
    }

    @Override
    public LFloat copy() {
        LFloat b = new LFloat(value);
        b.setContext(getContext());
        b.setPos(getPosStart(), getPosEnd());
        return b;
    }

    @Override
    public java.lang.Float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
