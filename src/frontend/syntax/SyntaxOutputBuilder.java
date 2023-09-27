package frontend.syntax;

public class SyntaxOutputBuilder {
    private static final StringBuilder syntaxOutput = new StringBuilder();

    public static void appendLine(String string) {
        if (!(string.equals("<BlockItem>") || string.equals("<Decl>") || string.equals("<BType>"))) {
            syntaxOutput.append(string).append('\n');
        }
    }

    public static String syntaxOutput() {
        return syntaxOutput.toString();
    }
}
