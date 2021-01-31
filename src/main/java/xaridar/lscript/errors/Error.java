package xaridar.lscript.errors;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.interpreting.Context;
import xaridar.lscript.lexing.Position;

public class Error {
    public Position posStart;
    public Position posEnd;
    String errorName;
    String details;

    public Error(Position posStart, Position posEnd, String name, String details) {
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.errorName = name;
        this.details = details;
    }

    @Override
    public String toString() {
        String result = String.format("%s: %s\nFile %s, line %d", errorName, details, posStart.getFn(), posStart.getLn() + 1);
        result += "\n\n" + ErrorUtilities.stringWithArrows(posStart.getFtxt(), posStart, posEnd);
        return result;
    }

    public static class IllegalCharError extends Error {
        public IllegalCharError(Position posStart, Position posEnd, String details) {
            super(posStart, posEnd, "Illegal Character", details);
        }
    }

    public static class ExpectedCharError extends Error {
        public ExpectedCharError(Position posStart, Position posEnd, String details) {
            super(posStart, posEnd, "Expected Character", details);
        }
    }

    public static class InvalidSyntaxError extends Error {
        public InvalidSyntaxError(Position posStart, Position posEnd, String details) {
            super(posStart, posEnd, "Invalid Syntax", details);
        }
    }

    public static class RunTimeError extends Error {
        Context context;
        public RunTimeError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Runtime Error", details);
            this.context = context;
        }

        public RunTimeError(Position posStart, Position posEnd, String name, String details, Context context) {
            super(posStart, posEnd, name, details);
            this.context = context;
        }

        @Override
        public String toString() {
            String result = generateStackTrace();
            result += String.format("%s: %s", errorName, details);
            result += "\n\n" + ErrorUtilities.stringWithArrows(posStart.getFtxt(), posStart, posEnd);
            return result;
        }

        private String generateStackTrace() {
            StringBuilder result = new StringBuilder();
            Position pos = posStart;

            while (context != null) {
                result.insert(0, String.format("\tFile %s, line %d, in %s\n", pos.getFn(), pos.getLn() + 1, context.getDisplayName()));
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

        public IndexOutOfBoundsError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Index Out Of Bounds", details, context);
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

    public static class ArgumentError extends RunTimeError {
        public ArgumentError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Argument Error", details, context);
        }
    }

    public static class IllegalAccessError extends RunTimeError {
        public IllegalAccessError(Position posStart, Position posEnd, String details, Context context) {
            super(posStart, posEnd, "Illegal Access", details, context);
        }
    }

}
