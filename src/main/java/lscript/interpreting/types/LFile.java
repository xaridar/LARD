package lscript.interpreting.types;

public class LFile extends Value {

    private final String path;
    private final String accessMode;

    public LFile(String path, String accessMode) {
        super("file");
        this.path = path;
        this.accessMode = accessMode;
    }

    @Override
    public LFile copy() {
        return (LFile) new LFile(path, accessMode).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Object getValue() {
        return "path";
    }

    @Override
    public String toString() {
        return "File: " + path;
    }

    public String getPath() {
        return path;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public boolean canRead() {
        return accessMode.equals("r");
    }

    public boolean canWrite() {
        return accessMode.equals("a") || accessMode.equals("w");
    }

    public boolean shouldOverwrite() {
        return accessMode.equals("w");
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
