package lscript.parsing.nodes;

import lscript.lexing.Position;
import lscript.lexing.Token;

import java.util.List;

public class ImportNode extends Node {
    private final Token fileName;
    private final List<Token> tokensToImport;
    private final List<String> names;

    /**
     * @param fileName - The name of the desired file.
     * @param tokensToImport - A list of Tokens representing variables and functions to import from the desired file.
     * @param names - A list of names to import variables and methods as.
     */
    public ImportNode(Token fileName, List<Token> tokensToImport, List<String> names) {
        super(fileName.getPosStart(), tokensToImport == null ? fileName.getPosEnd() : tokensToImport.get(tokensToImport.size() - 1).getPosEnd());
        this.fileName = fileName;
        this.tokensToImport = tokensToImport;
        this.names = names;
    }

    public List<Token> getTokensToImport() {
        return tokensToImport;
    }

    public Token getFileName() {
        return fileName;
    }

    public boolean importAll() {
        return tokensToImport == null;
    }

    public List<String> getNames() {
        return names;
    }
}
