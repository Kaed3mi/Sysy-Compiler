import backend.MipsBuilder;
import exceptions.ErrorBuilder;
import frontend.Visitor;
import frontend.lexical.Lexer;
import frontend.lexical.TokenList;
import frontend.syntax.Parser;
import frontend.syntax.SyntaxOutputBuilder;
import frontend.syntax.ast.Ast;
import midend.LLvmBuilder;

import java.io.*;

public class Compiler {
    private static final File testFile = new File("testfile.txt");
    private static final File syntaxFile = new File("output.txt");
    private static final File lexicalFile = new File("output.txt");
    private static final File errorFile = new File("error.txt");
    private static final File llvmFile = new File("llvm_ir.txt");
    private static final File mipsFile = new File("mips.txt");


    public static void main(String[] args) {
        try {
            // 词法分析
            String sourceCode = getSourceCode(testFile);
            Lexer lexer = new Lexer(sourceCode);
            TokenList tokenList = lexer.lex();
            // 语法分析
            Parser parser = new Parser(tokenList);
            Ast ast = parser.parse();
            Visitor visitor = new Visitor(ast);
            visitor.visit();

            build(tokenList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void build(TokenList tokenList) throws Exception {
        // buildLexical(tokenList);
        buildSyntax();
        // buildException();
        buildLLvm();
        buildMips();
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
        FileWriter fileWriter = new FileWriter(lexicalFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(tokenList.toString());
        bufferedWriter.close();
        fileWriter.close();
    }

    private static void buildSyntax() throws IOException {
        FileWriter fileWriter = new FileWriter(syntaxFile);
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

    private static void buildLLvm() throws Exception {
        FileWriter fileWriter = new FileWriter(llvmFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(LLvmBuilder.LLvmOutput());
        bufferedWriter.close();
        fileWriter.close();
    }

    private static void buildMips() throws Exception {
        FileWriter fileWriter = new FileWriter(mipsFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(MipsBuilder.buildMips());
        bufferedWriter.close();
        fileWriter.close();
    }
}