package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */


public class Module extends Value {

    private final String name;

    /**
     * @param name - The name associated with the module.
     */
    public Module(String name) {
        super("module");
        this.name = name;
    }

    @Override
    public Value copy() {
        return new Module(name).setContext(context).setPos(posStart, posEnd);
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof Module))
            return (LBoolean) new LBoolean(false).setContext(context).setPos(posStart, posEnd);
        return (LBoolean) new LBoolean(name.equals(((Module) other).getName())).setContext(context).setPos(posStart, posEnd);
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        return (LBoolean) new LBoolean(!equalTo(other).isTrue()).setContext(context).setPos(posStart, posEnd);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<module " + name + ">";
    }
}
