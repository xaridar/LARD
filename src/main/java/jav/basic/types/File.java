package jav.basic.types;

import jav.Tuple;
import jav.basic.Error;

public class File extends Value {

    private final String path;
    private final String accessMode;

    public File(String path, String accessMode) {
        super("file");
        this.path = path;
        this.accessMode = accessMode;
    }

    @Override
    public File copy() {
        return (File) new File(path, accessMode).setContext(getContext()).setPos(getPosStart(), getPosEnd());
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
    public Boolean equalTo(Value other) {
        if (!(other instanceof File)) {
            return (Boolean) new Boolean(false).setContext(getContext()).setPos(getPosStart(), getPosEnd());
        }
        return (Boolean) new Boolean(path.equals(((File) other).getPath())).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public Boolean notEqualTo(Value other) {
        return (Boolean) new Boolean(!equalTo(other).getValue()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }
}
