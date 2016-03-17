package seers.astvisitortest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	/**
	 * List of methods
	 */
	private List<Method> methods;
	/**
	 * List of fields
	 */
	private List<Field> fields;

	/**
	 * Default constructor
	 */
	public GeneralVisitor() {
		methods = new ArrayList<>();
		fields = new ArrayList<>();
	}

    public List<Method> getMethods() {
        return methods;
    }

    public List<Field> getFields() {
        return fields;
    }

    /**
	 * Method that visits the method declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		SimpleName name = node.getName();

        // add the name of the method to the list
		methods.add(new Method(name.getFullyQualifiedName(), node.parameters()));

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
        String type = node.getType().toString();
        for (VariableDeclarationFragment fragment : varFragments) {

			// add the name and type of the field
			fields.add(new Field(fragment.getName().getFullyQualifiedName(), type));
		}
		return super.visit(node);
	}
}
