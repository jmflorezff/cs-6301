package team3;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import seers.astvisitortest.VariableVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Consumer;

public class Main {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		// file to parse
		// String baseFolder = "SampleCode";
		// String filePath = baseFolder + File.separator + "Countdown.java";
		// File file = new File(filePath);

		// String filepath = "SourceCode" + File.separator + "freemind";
		// String fileName = "part2.freemind.txt";
		String filepath = "SourceCode" + File.separator + "weka";
		String fileName = "part2.weka.txt";
		File folder = new File(filepath);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				System.out.println(file.getName());

				FileWriter writer = new FileWriter(fileName, true);
				writer.write(file.getName() + "\r\n");
				writer.close();

				// parse the file
				CompilationUnit compUnit = parseFile(file);

				compUnit.types().forEach(new Consumer() {
					@Override
					public void accept(Object o) {
						if (o instanceof EnumDeclaration) {
							FileWriter wr;
							try {
								wr = new FileWriter(fileName, true);
								wr.write("ENUM class skipped.");
								wr.close();
							} catch (IOException e) {
							}
						}
						if (o instanceof TypeDeclaration) {
							TypeDeclaration a = (TypeDeclaration) o;
							a.accept(new UnusedVariableVisitor());
						}
					}
				});

				System.out.println();
				writer = new FileWriter(fileName, true);
				writer.write("\r\n");
				writer.close();
			}
		}
	}
	// }

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
