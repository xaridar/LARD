package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A Node that represents an 'import' token followed by a filename.
 */
public class FileImportNode extends Node {
    private final Token fileName;
    private String name;

    /**
     * @param fileName - The name of the desired file.
     * @param name - A symbol name to import the file as. Can be null.
     */
    public FileImportNode(Token fileName, String name) {
        super(fileName.getPosStart().copy(), fileName.getPosEnd().copy());
        this.fileName = fileName;
        this.name = name;
        if (name == null) {
            this.name = fileName.getValue().toString().split("[\\\\/]")[fileName.getValue().toString().split("[\\\\/]").length - 1];
        }
    }

    /**
     * @return The name of the desired file.
     */
    public Token getFileName() {
        return fileName;
    }

    /**
     * @return A symbol name to import the file as.
     */
    public String getName() {
        return name;
    }
}
