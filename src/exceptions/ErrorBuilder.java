package exceptions;

import java.util.ArrayList;
import java.util.Comparator;

public class ErrorBuilder {
    private static final ArrayList<CompileError> ErrorList = new ArrayList<>();

    public static void appendError(CompileError compileError) {
        ErrorList.add(compileError);
    }

    public static String errorOutput() {
        ErrorList.sort(Comparator.comparingInt(CompileError::getLine));
        StringBuilder errorOutput = new StringBuilder();
        ErrorList.forEach(e -> errorOutput.append(e).append("\n"));
        return errorOutput.toString();
    }
}
