package lscript.errors;

import lscript.interpreting.Context;
import lscript.lexing.Position;

public class Error {
    Position pos_start;
    Position pos_end;
    String error_name;
    String details;

    public Error(Position pos_start, Position pos_end, String name, String details) {
        this.pos_start = pos_start;
        this.pos_end = pos_end;
        this.error_name = name;
        this.details = details;
    }

    @Override
    public String toString() {
        String result = String.format("%s: %s\nFile %s, line %d", error_name, details, pos_start.getFn(), Integer.valueOf(pos_start.getLn() + 1));
        result += "\n\n" + ErrorUtil.string_with_arrows(pos_start.getFtxt(), pos_start, pos_end);
        return result;
    }

    public static class IllegalCharError extends Error {
        public IllegalCharError(Position pos_start, Position pos_end, String details) {
            super(pos_start, pos_end, "Illegal Character", details);
        }
    }

    public static class ExpectedCharError extends Error {
        public ExpectedCharError(Position pos_start, Position pos_end, String details) {
            super(pos_start, pos_end, "Expected Character", details);
        }
    }

    public static class InvalidSyntaxError extends Error {
        public InvalidSyntaxError(Position pos_start, Position pos_end, String details) {
            super(pos_start, pos_end, "Invalid Syntax", details);
        }
    }

    public static class RunTimeError extends Error {
        Context context;
        public RunTimeError(Position pos_start, Position pos_end, String details, Context context) {
            super(pos_start, pos_end, "Runtime Error", details);
            this.context = context;
        }

        public RunTimeError(Position pos_start, Position pos_end, String name, String details, Context context) {
            super(pos_start, pos_end, name, details);
            this.context = context;
        }

        @Override
        public String toString() {
            String result = generate_stack_trace();
            result += String.format("%s: %s", error_name, details);
            result += "\n\n" + ErrorUtil.string_with_arrows(pos_start.getFtxt(), pos_start, pos_end);
            return result;
        }

        private String generate_stack_trace() {
            StringBuilder result = new StringBuilder();
            Position pos = pos_start;

            while (context != null) {
                result.insert(0, String.format("\tFile %s, line %d, in %s\n", pos.getFn(), Integer.valueOf(pos.getLn() + 1), context.getDisplayName()));
                pos = context.getParentEntryPos();
                context = context.getParent();
            }

            return "Traceback (most recent call last):\n" + result.toString();
        }
    }

    public static class UnsupportedOperationError extends RunTimeError {
        public UnsupportedOperationError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Unsupported Operation", details, context);
        }
    }

    public static class IndexOutOfBoundsError extends RunTimeError {

        public IndexOutOfBoundsError(Position pos_start, Position pos_end, String details, Context context) {
            super(pos_start, pos_end, "Index Out Of Bounds", details, context);
        }
    }

    public static class FileAccessError extends RunTimeError {
        public FileAccessError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "File Access Error", details, context);
        }
    }

    public static class ImportError extends RunTimeError {
        public ImportError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Import Error", details, context);
        }
    }
}
