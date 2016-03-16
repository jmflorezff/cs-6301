package seers.astvisitortest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.omg.Dynamic.Parameter;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	/**
	 * List of methods
	 */
	private List<String> methods;
	/**
	 * List of fields
	 */
	private List<String> fields;
	/**
	 * List of parameters
	 */
	private List<String> parameters;
	/**
	 * List of variable declarations
	 */
	private List<String> declarations;
	/**
	 * List of parameters
	 */
	private List<String> formattedMethods;
	/**
	 * List of variable declarations
	 */
	private List<String> formattedVariables;

	/**
	 * Default constructor
	 */
	public GeneralVisitor() {
		methods = new ArrayList<>();
		fields = new ArrayList<>();
		parameters = new ArrayList<>();
		declarations = new ArrayList<>();
		formattedMethods = new ArrayList<>();
		formattedVariables = new ArrayList<>();
	}

	/**
	 * Method that visits the method declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration node) {

		// add the name of the method to the list
		SimpleName name = node.getName();
		methods.add(name.getFullyQualifiedName());

		String formattedMethod = "M: " + name.getFullyQualifiedName() + " (";

		// For every visit to a method we get the parameters
		List<SingleVariableDeclaration> paramList = node.parameters();
		for (SingleVariableDeclaration param : paramList) {
			parameters.add(param.getName().getFullyQualifiedName());

			formattedMethod += param.getName().getFullyQualifiedName() + ":" + param.getType().toString() + ", ";
		}
		formattedMethod = formattedMethod.substring(0, formattedMethod.length() - 2);
		formattedMethod += ") ";

		formattedMethods.add(formattedMethod);

		return super.visit(node);
	}

	/**
	 * Method that visits the field declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		// get the fragments of the field declaration
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {
			// add the name of the field
			fields.add(fragment.getName().getFullyQualifiedName());// Format V: variable_name:type
			String variable = "V: " + fragment.getName().getFullyQualifiedName() + ":" + node.getType().toString();
			formattedVariables.add(variable);
		}
		return super.visit(node);
	}

	// We write custom method to get variable declarations
	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		// get the fragments of the field declaration
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {
			// add the name of the field
			declarations.add(fragment.getName().getFullyQualifiedName());
			// Format V: variable_name:type
			String variable = "V: " + fragment.getName().getFullyQualifiedName() + ":" + node.getType().toString();
			formattedVariables.add(variable);
		}
		return super.visit(node);
	}

	public List<String> getMethods() {
		return methods;
	}

	public List<String> getFields() {
		return fields;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public List<String> getDeclarations() {
		return declarations;
	}

	public List<String> getFormattedMethods() {
		return formattedMethods;
	}

	public List<String> getFormattedVariables() {
		return formattedVariables;
	}

}
