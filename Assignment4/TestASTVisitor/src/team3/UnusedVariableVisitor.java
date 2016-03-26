package team3;

import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by juan on 3/21/16. Hi
 */
@SuppressWarnings("unchecked")
public class UnusedVariableVisitor extends ASTVisitor {

	private CompilationUnit root;

	private Map<String, Integer> fieldDeclarations;
	private Map<String, Integer> currentVariableDeclarations;
	private Set<String> variableWrites;

	private Set<String> fieldUses;
	private Set<String> localUses;

	public UnusedVariableVisitor() {
	}

	public UnusedVariableVisitor(CompilationUnit root) {
		this.root = root;
		variableWrites = new HashSet<>();
	}

	private void reportUnusedVariables(Map<String, Integer> declarations, final Set<String> uses, final String type) {
		declarations.forEach(new BiConsumer<String, Integer>() {
			@Override
			public void accept(String variableName, Integer lineNumber) {
				if (!uses.contains(variableName)) {
					System.out.println(
							String.format("* The [%s] [%s] is declared but " + "never read in the code (line:[%d])",
									type, variableName, lineNumber));
				}
			}
		});
	}

	private void addDeclaration(VariableDeclaration declaration, Map<String, Integer> map) {
		map.put(declaration.getName().getFullyQualifiedName(), root.getLineNumber(declaration.getStartPosition()));
	}

	/**
	 * Visits FieldDeclarations, getsFragments and iterates. New Consumer is
	 * just an equivalent of 'for (VariableDeclarationFragment fragment :
	 * node.fragments())' Gets a fragment and adds to the corresponding Map
	 * Variable
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean visit(FieldDeclaration node) {
		node.fragments().forEach(new Consumer() {
			@Override
			public void accept(Object o) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;
				addDeclaration(fragment, fieldDeclarations);
			}
		});
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		node.fragments().forEach(new Consumer() {
			@Override
			public void accept(Object o) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;
				addDeclaration(fragment, currentVariableDeclarations);
			}
		});

		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean visit(SimpleName node) {
		String name = node.getFullyQualifiedName();

		if (currentVariableDeclarations != null && currentVariableDeclarations.containsKey(name)) {
			localUses.add(name);
		} else {
			fieldUses.add(name);
		}

		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		// TODO: last name from left hand side of assignment is not a read
		
		String[] rightHandElements = node.getRightHandSide().toString().replace('(', ' ').replace(')', ' ').replaceAll("this.", " ").replace("\n", " ").replace("\r", " ").split(" ");
		if (rightHandElements.length > 0) {
			for (int i = 0; i < rightHandElements.length; i++) {
				localUses.add(rightHandElements[i].trim());
				fieldUses.add(rightHandElements[i].trim());
			}
		}
		return false;
	}

	@Override
	public void endVisit(Assignment node) {
		//reportUnusedVariables(currentVariableDeclarations, localUses, "variableWrites");
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// Structures used for local variables must be reconstructed every time
		// a new method is
		// about to be visited
		currentVariableDeclarations = new HashMap<>();
		localUses = new HashSet<>();

		// Add method parameters as locally defined variables
		node.parameters().forEach(new Consumer<ASTNode>() {
			@Override
			public void accept(ASTNode node) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) node;
				addDeclaration(param, currentVariableDeclarations);
			}
		});

		node.getBody().accept(this);

		return false;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		reportUnusedVariables(currentVariableDeclarations, localUses, "variable");
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		fieldDeclarations = new HashMap<>();
		fieldUses = new HashSet<>();

		if (root == null) {
			root = (CompilationUnit) node.getRoot();
		}

		for (FieldDeclaration fieldDeclaration : node.getFields()) {
			fieldDeclaration.accept(this);
		}

		for (MethodDeclaration methodDeclaration : node.getMethods()) {
			methodDeclaration.accept(this);
		}

		// Delegate visiting of nested classes to other instances
		for (TypeDeclaration typeDeclaration : node.getTypes()) {
			typeDeclaration.accept(new UnusedVariableVisitor(root));
		}

		return false;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		reportUnusedVariables(fieldDeclarations, fieldUses, "field");
	}
}
