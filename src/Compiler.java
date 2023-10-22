import exceptions.ErrorBuilder;
import frontend.Visitor;
import frontend.lexical.Lexer;
import frontend.lexical.TokenList;
import frontend.syntax.Parser;
import frontend.syntax.SyntaxOutputBuilder;
import frontend.syntax.ast.Ast;

import java.io.*;

public class Compiler {
    private static File inputFile;
    private static File outputFile;
    private static File errorFile;

    public static void main(String[] args) {
        try {
            fileProcess();
            // 词法分析
            String sourceCode = getSourceCode(inputFile);
            Lexer lexer = new Lexer(sourceCode);
            TokenList tokenList = lexer.lex();
            // buildLexical(tokenList);
            // 语法分析
            Parser parser = new Parser(tokenList);
            Ast ast = parser.parse();
            // buildSyntax();
            Visitor visitor = new Visitor(ast);
            visitor.visit();
            buildException();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void fileProcess() {
        inputFile = new File("testfile.txt");
        outputFile = new File("output.txt");
        errorFile = new File("error.txt");
    }

    private static String getSourceCode(File inputFile) throws Exception {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        bufferedReader.close();
        fileReader.close();
        return sb.toString();
    }

    private static void buildLexical(TokenList tokenList) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(tokenList.toString());
        bufferedWriter.close();
        fileWriter.close();
    }

    private static void buildSyntax() throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(SyntaxOutputBuilder.syntaxOutput());
        bufferedWriter.close();
        fileWriter.close();
    }

    private static void buildException() throws Exception {
        FileWriter fileWriter = new FileWriter(errorFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(ErrorBuilder.errorOutput());
        bufferedWriter.close();
        fileWriter.close();
    }
}