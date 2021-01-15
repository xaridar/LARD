package jav.basic.types;

public class Int extends Number {

    private int value;

    public Int(int value) {
        super("int", value);
        this.value = value;
    }

    @Override
    public Int copy() {
        Int b = new Int(value);
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
