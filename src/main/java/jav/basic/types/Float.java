package jav.basic.types;

public class Float extends Number {

    private float value;

    public Float(float value) {
        super("float", value);
        this.value = value;
    }

    public Float(int value) {
        super("float", value);
        this.value = value;
    }

    @Override
    public Float copy() {
        Float b = new Float(value);
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
