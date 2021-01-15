package jav;

import jav.basic.Position;

public class Util {
    public static String string_with_arrows(String text, Position pos_start, Position pos_end) {
        StringBuilder result = new StringBuilder();

        String sub = text.substring(0, pos_start.getIdx());
        int idx_start = Math.max(sub.lastIndexOf('\n'), 0);
        int idx_end = text.indexOf('\n', idx_start + 1);
        if (idx_end< 0)
            idx_end = text.length();

        int line_count = pos_end.getLn() - pos_start.getLn() + 1;
        for (int i = 0; i < line_count; i++) {

            String line = text.substring(idx_start, idx_end);
            int col_start = i == 0 ? pos_start.getCol() : 0;
            int col_end = i == line_count - 1 ? pos_end.getCol() : line.length() - 1;

            result.append(line).append('\n');
            result.append(" ".repeat(col_start)).append("^".repeat(col_end - col_start));

            idx_start = idx_end;
            idx_end = text.indexOf('\n', idx_start + 1);
            if (idx_end< 0)
                idx_end = text.length();
        }

        return result.toString().replace("\t", "");
    }

}
