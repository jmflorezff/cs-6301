package seers.astvisitortest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Program that extracts AST info from a Java file
 * 
 * @author ojcch
 *
 */
public class MainVisitor {

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// file to parse
		String baseFolder = "jabref-src";
		String filePath = baseFolder + File.separator + "GrammarBasedSearchRule.java";
		File file = new File("test/test/TestClass.java");

		// parse the file
		CompilationUnit compUnit = parseFile(file);

		// create and accept the visitor
		/*
		 * GeneralVisitor visitor = new GeneralVisitor();
		 * compUnit.accept(visitor);
		 * 
		 * // print the list of methods and fields of the Java file List<String>
		 * fields = visitor.getFields(); List<String> methods =
		 * visitor.getMethods(); List<String> parameters =
		 * visitor.getParameters(); List<String> declarations =
		 * visitor.getDeclarations();
		 * 
		 * List<String> formattedMethods = visitor.getFormattedMethods();
		 * List<String> formattedVariables = visitor.getFormattedVariables();
		 * 
		 * 
		 * for (String field : fields) { System.out.println("Field: " + field);
		 * } for (String method : methods) { System.out.println("Method: " +
		 * method); } for (String parameter : parameters) { System.out.println(
		 * "Parameter: " + parameter); } for (String declaration : declarations)
		 * { System.out.println("Declarations: " + declaration); }
		 * 
		 * for (String s : formattedMethods) { System.out.println(s); } for
		 * (String s : formattedVariables) { System.out.println(s); }
		 */
		/* Print methods with its parameters in the format */
		VariableVisitor varVisitor = new VariableVisitor();
		compUnit.accept(varVisitor);

		List<String> assignmentsLeftHandSide = varVisitor.getAssignments();
		List<String> variables = varVisitor.getVariables();
		List<String> variableWrite = varVisitor.getVariableWrite();

		/*System.out.println("Assignment operations");
		for (String s : assignmentsLeftHandSide) {
			System.out.println(s);
		}

		System.out.println("Write operations");
		for (String s : variableWrite) {
			System.out.println(s);
		}

		System.out.println("Variables and Fields");
		for (String s : variables) {
			System.out.println(s);
		}*/
		ArrayList<String> r1 = intersection(variables, assignmentsLeftHandSide);
		
		ArrayList<String> r2 = intersection(r1, variableWrite);
	
		
		System.out.println("Variables not written");
		for (String s : r2) {
			System.out.println(s);
		}
	}

	/**
	 * Computes the intersection of two Lists of Strings, returning it as a new
	 * ArrayList of Strings
	 *
	 * @param list1
	 *            one of the Lists from which to compute an intersection
	 * @param list2
	 *            one of the Lists from which to compute an intersection
	 *
	 * @return a new ArrayList of Strings containing the intersection of list1
	 *         and list2
	 */
	public static ArrayList<String> intersection(List<String> list1, List<String> list2) {
		ArrayList<String> result = new ArrayList<String>(list1);

		result.retainAll(list2);

		return result;
	}

	/**
	 * Parses a java file
	 * 
	 * @param file
	 *            the file to parse
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
	 * @param parser
	 *            the AST parser
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
}
