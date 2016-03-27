package seers.astvisitortest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VariableVisitor extends ASTVisitor {
	// Variable list: local and fields
	private List<String> variables;

	private List<String> assignments;

	private List<String> variableWrite;

	public List<String> getVariables() {
		return variables;
	}

	public List<String> getAssignments() {
		return assignments;
	}

	public List<String> getVariableWrite() {
		return variableWrite;
	}

	public VariableVisitor() {
		variables = new ArrayList<>();
		assignments = new ArrayList<>();
		variableWrite = new ArrayList<>();
	}

	public boolean visit(FieldDeclaration node) {
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {
			variables.add(fragment.getName().getFullyQualifiedName());
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {
			variables.add(fragment.getName().getFullyQualifiedName());
		}
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
		variableWrite.add(node.getLeftHandSide().toString());
		return super.visit(node);
	}

	public boolean visit(FieldAccess node) {
		assignments.add(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}

}
