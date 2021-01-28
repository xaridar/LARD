package xaridar.lscript.errors;

import xaridar.lscript.lexing.Position;

public class ErrorUtilities {
    public static String stringWithArrows(String text, Position posStart, Position posEnd) {
        StringBuilder result = new StringBuilder();

        String sub = text.substring(0, posStart.getIdx());
        int idxStart = Math.max(sub.lastIndexOf('\n'), 0);
        int idxEnd = text.indexOf('\n', idxStart + 1);
        if (idxEnd< 0)
            idxEnd = text.length();

        int lineCount = posEnd.getLn() - posStart.getLn() + 1;
        for (int i = 0; i < lineCount; i++) {

            String line = text.substring(idxStart, idxEnd);
            int colStart = i == 0 ? posStart.getCol() : 0;
            int colEnd = i == lineCount - 1 ? posEnd.getCol() : line.length() - 1;

            result.append(line).append('\n');
            result.append(new String(new char[colStart]).replace("\0", " ")).append(new String(new char[colEnd - colStart]).replace("\0", "^"));

            idxStart = idxEnd;
            idxEnd = text.indexOf('\n', idxStart + 1);
            if (idxEnd< 0)
                idxEnd = text.length();
        }

        return result.toString().replace("\t", "");
    }

}
