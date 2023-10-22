package exceptions;

public class CompileError {

    private final static boolean ErrorDetail = false;
    private final int line;
    private final ErrorType type;
    private final String message;

    public CompileError(int line, ErrorType type) {
        this.line = line;
        this.type = type;
        this.message = "";
    }

    public CompileError(int line, ErrorType type, String message) {
        this.line = line;
        this.type = type;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        if (ErrorDetail) {
            return String.format("%d %s %s", line, type, message);
        } else {
            return String.format("%d %s", line, type);
        }
    }
}
