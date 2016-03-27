package team3;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by juan on 3/21/16.
 * Hi
 */
@SuppressWarnings("unchecked")
public class UnusedVariableVisitor extends ASTVisitor {

    private CompilationUnit root;

    private Map<String, Integer> fieldDeclarations;
    private Map<String, Integer> currentVariableDeclarations;
    private Set<String> fieldUses;
    private Set<String> localUses;
    private List<MessageData> messages = new ArrayList<>();

    private class MessageData {
        private String variableType;
        private String variableName;
        private int lineNumber;

        public MessageData(String variableType, String variableName, int lineNumber) {
            this.variableType = variableType;
            this.variableName = variableName;
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return String.format("* The [%s] [%s] is declared but " +
                    "never read in the code (line:[%d])", variableType, variableName, lineNumber);
        }
    }

    private UnusedVariableVisitor() {
    }

    private UnusedVariableVisitor(CompilationUnit root) {
        this.root = root;
    }

    public static void processFile(String inputFileName) throws IOException {
        int lastSep = inputFileName.lastIndexOf(File.separator);
        int lastDot = inputFileName.lastIndexOf(".");

        if (lastDot < 0 || !".java".equals(inputFileName.substring(lastDot))) {
            return;
        }

        String outputFileName = inputFileName.substring(lastSep >= 0 ? lastSep + 1 : 0,
                lastDot >= 0 ? lastDot : inputFileName.length()) + ".txt";
        File outputFile = new File(outputFileName);

        if (!outputFile.exists()) {
            final boolean[] delete = {false};
            File inputFile = new File(inputFileName);
            CompilationUnit compUnit = parseFile(inputFile);

            try (PrintWriter writer = new PrintWriter(outputFileName)) {
                writer.println("File: " + inputFileName.substring(lastSep + 1));

                compUnit.types().forEach(new Consumer() {
                    @Override
                    public void accept(Object o) {
                        UnusedVariableVisitor visitor = new UnusedVariableVisitor();
                        ((ASTNode) o).accept(visitor);
                        if (visitor.messages.size() == 0) {
                            delete[0] = true;
                        } else {
                            visitor.printWarnings(writer);
                        }
                    }
                });
            }

            if (delete[0]) {
                boolean delete1 = outputFile.delete();
            }
        }
    }

    private void printWarnings(PrintWriter writer) {
        for (MessageData message : messages) {
            writer.println(message.toString());
        }
    }

    /**
     * Parses a java file
     *
     * @param file the file to parse
     * @return the CompilationUnit of a java file (i.e., its AST)
     * @throws IOException
     */
    private static CompilationUnit parseFile(File file) throws IOException {

        // read the content of the file
        char[] fileContent = FileUtils.readFileToString(file).toCharArray();

        // create the AST parser
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setUnitName(file.getName());
        parser.setSource(fileContent);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        // set some default configuration
        setParserConfiguration(parser);

        // parse and return the AST
        return (CompilationUnit) parser.createAST(null);

    }

    /**
     * Sets the default configuration of an AST parser
     *
     * @param parser the AST parser
     */
    public static void setParserConfiguration(ASTParser parser) {
        @SuppressWarnings("unchecked")
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);

        parser.setCompilerOptions(options);
        parser.setResolveBindings(true);

        // parser.setEnvironment(classPaths, sourceFolders, encodings, true);
    }

    private void storeUnusedVariables(Map<String, Integer> declarations, final Set<String> uses,
                                      final String type) {
        declarations.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String variableName, Integer lineNumber) {
                if (!uses.contains(variableName)) {
                    messages.add(new MessageData(type, variableName, lineNumber));
//                    System.out.println(String.format("* The [%s] [%s] is declared but " +
//                            "never read in the code (line:[%d])", type, variableName, lineNumber));
                }
            }
        });
    }

    private void addDeclaration(VariableDeclaration declaration, Map<String, Integer> map) {
        int lineNumber = root.getLineNumber(declaration.getStartPosition());
        if (map != null) {
            map.put(declaration.getName().getFullyQualifiedName(),
                    lineNumber);
        }
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        visitFragments(node.fragments(), fieldDeclarations);
        return false;
    }

    private void visitFragments(List fragments, Map<String, Integer> map) {
        for (Object o : fragments) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;
            addDeclaration(fragment, map);
            if (fragment.getInitializer() != null) {
                fragment.getInitializer().accept(this);
            }
        }
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        visitFragments(node.fragments(), currentVariableDeclarations);

        return false;
    }

    private void storeName(String name) {
        if (currentVariableDeclarations != null && currentVariableDeclarations.containsKey(name)) {
            localUses.add(name);
        } else {
            fieldUses.add(name);
        }
    }

    @Override
    public boolean visit(SimpleName node) {
        String name = node.getFullyQualifiedName();
        storeName(name);

        return true;
    }

    @Override
    public boolean visit(Assignment node) {
        node.getRightHandSide().accept(this);
        node.getLeftHandSide().accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName node) {
                return false;
            }

            @Override
            public boolean visit(QualifiedName node) {
                node.getQualifier().accept(UnusedVariableVisitor.this);
                return false;
            }

            @Override
            public boolean visit(FieldAccess node) {
                node.getExpression().accept(UnusedVariableVisitor.this);
                return false;
            }

            @Override
            public boolean visit(ArrayAccess node) {
                // Traverse it normally
                node.getArray().accept(UnusedVariableVisitor.this);
                node.getIndex().accept(UnusedVariableVisitor.this);
                return false;
            }
        });
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // Structures used for local variables must be reconstructed every time a new method is
        // about to be visited
        currentVariableDeclarations = new HashMap<>();
        localUses = new HashSet<>();

        // Add method parameters as locally defined variables
//        node.parameters().forEach(new Consumer<ASTNode>() {
//            @Override
//            public void accept(ASTNode node) {
//                SingleVariableDeclaration param = (SingleVariableDeclaration) node;
//                addDeclaration(param, currentVariableDeclarations);
//            }
//        });

        Block body = node.getBody();
        if (body != null) {
            node.getBody().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        storeUnusedVariables(currentVariableDeclarations, localUses, "variable");
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        fieldDeclarations = new HashMap<>();
        fieldUses = new HashSet<>();

        if (root == null) {
            root = (CompilationUnit) node.getRoot();
        }

//        for (FieldDeclaration fieldDeclaration : node.getFields()) {
//            fieldDeclaration.accept(this);
//        }
//
//        for (MethodDeclaration methodDeclaration : node.getMethods()) {
//            methodDeclaration.accept(this);
//        }
//
//        // Delegate visiting of nested classes to other instances
//        for (TypeDeclaration typeDeclaration : node.getTypes()) {
//            UnusedVariableVisitor visitor = new UnusedVariableVisitor(root);
//            typeDeclaration.accept(visitor);
//            messages.addAll(visitor.messages);
//        }
        for (Object o : node.bodyDeclarations()) {
            ASTNode astNode = ((ASTNode) o);
            if (astNode instanceof AbstractTypeDeclaration) {
                UnusedVariableVisitor visitor = new UnusedVariableVisitor(root);
                astNode.accept(visitor);
                messages.addAll(visitor.messages);
            } else {
                astNode.accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        storeUnusedVariables(fieldDeclarations, fieldUses, "field");
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        fieldDeclarations = new HashMap<>();
        fieldUses = new HashSet<>();

        if (root == null) {
            root = (CompilationUnit) node.getRoot();
        }

        for (Object o : node.bodyDeclarations()) {
            ASTNode astNode = ((ASTNode) o);
            if (astNode instanceof AbstractTypeDeclaration) {
                UnusedVariableVisitor visitor = new UnusedVariableVisitor(root);
                astNode.accept(visitor);
                messages.addAll(visitor.messages);
            } else {
                astNode.accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(EnumDeclaration node) {
        storeUnusedVariables(fieldDeclarations, fieldUses, "field");
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        fieldDeclarations = new HashMap<>();
        fieldUses = new HashSet<>();

        if (root == null) {
            root = (CompilationUnit) node.getRoot();
        }

        for (Object o : node.bodyDeclarations()) {
            ASTNode astNode = ((ASTNode) o);
            if (astNode instanceof AbstractTypeDeclaration) {
                UnusedVariableVisitor visitor = new UnusedVariableVisitor(root);
                astNode.accept(visitor);
                messages.addAll(visitor.messages);
            } else {
                astNode.accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node) {
        storeUnusedVariables(fieldDeclarations, fieldUses, "field");
    }
}
