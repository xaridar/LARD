package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

/**/
public class LFile extends Value {

    private final String path;
    private final String accessModes;

    public LFile(String path, String accessModes) {
        super("file");
        this.path = path;
        this.accessModes = accessModes;
    }

    @Override
    public LFile copy() {
        return (LFile) new LFile(path, accessModes).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Object getValue() {
        return path;
    }

    @Override
    public String toString() {
        return "File: " + path;
    }

    public String getPath() {
        return path;
    }

    public String getAccessModes() {
        return accessModes;
    }

    public boolean canRead() {
        return accessModes.contains("r") || accessModes.contains("+");
    }

    public boolean canWrite() {
        return accessModes.contains("a") || accessModes.contains("w") || accessModes.contains("+");
    }

    public boolean shouldOverwrite() {
        return accessModes.contains("w");
    }

    public boolean binaryAccess() {
        return accessModes.contains("b");
    }

    @Override
    public LBoolean equalTo(Value other) {
        if (!(other instanceof LFile)) {
            return (LBoolean) new LBoolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (LBoolean) new LBoolean(path.equals(((LFile) other).getPath())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public LBoolean notEqualTo(Value other) {
        return (LBoolean) new LBoolean(!equalTo(other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
