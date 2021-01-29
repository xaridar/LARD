package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */


public class LFloat extends LNumber {

    private final float value;

    public LFloat(float value) {
        super("float", Float.valueOf(value));
        this.value = value;
    }

    public LFloat(int value) {
        super("float", Float.valueOf(value));
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
        return Float.valueOf(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
