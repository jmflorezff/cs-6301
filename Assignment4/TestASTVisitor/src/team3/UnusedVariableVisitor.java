package team3;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 * Created by Juan Florez on 3/21/16. Modified by Eduardo Jaime on 3/25/2016.
 */
/**
 * @author Eduardo Jaime
 *
 */
@SuppressWarnings("unchecked")
public class UnusedVariableVisitor extends ASTVisitor {

	private CompilationUnit root;

	PrintWriter writer;

	private Map<String, Integer> fieldDeclarations;
	private Map<String, Integer> currentVariableDeclarations;

	private Set<String> fieldUses;
	private Set<String> localUses;

	public UnusedVariableVisitor() {
		//this.writer = writer;
	}

	public UnusedVariableVisitor(CompilationUnit root) {
		this.root = root;
	}

	private void addDeclaration(VariableDeclaration declaration, Map<String, Integer> map) {
		map.put(declaration.getName().getFullyQualifiedName(), root.getLineNumber(declaration.getStartPosition()));
	}

	private void reportUnusedVariables(Map<String, Integer> declarations, final Set<String> uses, final String type) {
		declarations.forEach(new BiConsumer<String, Integer>() {
			@Override
			public void accept(String variableName, Integer lineNumber) {
				// System.out.println(variableName);
				if (!uses.contains(variableName)) {
					System.out.println(
							String.format("* The [%s] [%s] is declared but " + "never read in the code (line:[%d])",
									type, variableName, lineNumber));
					/*writer.println(
							String.format("* The [%s] [%s] is declared but " + "never read in the code (line:[%d])",
									type, variableName, lineNumber));*/
				}
			}
		});
	}

	@Override
	public boolean visit(SimpleName node) {
		String name = node.getFullyQualifiedName();
		addVariableUse(name);
		return true;
	}

	private void addVariableUse(String name) {
		if (currentVariableDeclarations != null && currentVariableDeclarations.containsKey(name)) {
			localUses.add(name);
		} else {
			fieldUses.add(name);
		}
	}

	/* Visit Methods */
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
		/*
		 * System.out.println("---------------------------");
		 * System.out.println("Field Uses");
		 * System.out.println("---------------------------"); for (String s :
		 * fieldUses) { System.out.println(s); }
		 */
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

	/**
	 * Gets the left hand side and right hand side of an assignment Leaves out
	 * literal such as string or numerical and processes only Variable or Field
	 * names.
	 */
	@Override
	public boolean visit(Assignment node) {
		// TODO: last name from left hand side of assignment is not a read

		// Get the right hand side of the expression.
		// If it an InfixExpression (Example: Numerical + 1)
		if (node.getRightHandSide() instanceof InfixExpression) {
			// right = leftOperand [operator] rightOperand [operator]
			// [operands....]

			InfixExpression right = (InfixExpression) node.getRightHandSide();

			// Get leftmost operand
			if (right.getLeftOperand() != null && right.getLeftOperand() instanceof SimpleName) {
				addVariableUse(right.getLeftOperand().toString());
			}
			// Get next immediate operand
			if (right.getRightOperand() != null && right.getRightOperand() instanceof SimpleName) {
				addVariableUse(right.getRightOperand().toString());
			}

			// Gets the rest of the operands in the expression
			Object[] operands = right.extendedOperands().toArray();
			for (int i = 0; i < operands.length; i++) {
				if (operands[i] instanceof SimpleName) {
					addVariableUse(operands[i].toString());
				}
			}
		}

		// Gets the right hand operand when there is only one in the expression
		// leaves out any other than variable/field names
		if (node.getRightHandSide() instanceof SimpleName) {
			addVariableUse(node.getRightHandSide().toString());
		}

		// Get parameters of right hand side method invocations
		if (node.getRightHandSide() instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) node.getRightHandSide();
			List<Object> args = method.arguments();
			Iterator<Object> iterator = args.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof SimpleName) {
					String name = obj.toString();
					addVariableUse(name);
				}
			}
		}

		// Gets rightmost operand in the left hand side of the expression
		if (node.getLeftHandSide() instanceof InfixExpression) {

			InfixExpression Left = (InfixExpression) node.getRightHandSide();

			Object[] operands = Left.extendedOperands().toArray();
			if (operands.length > 1) {
				addVariableUse(operands[operands.length - 1].toString());
			}
		}

		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// Structures used for local variables must be reconstructed every time
		// a new method is about to be visited
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
		/*
		 * System.out.println("---------------------------");
		 * System.out.println("Method Uses");
		 * System.out.println("---------------------------"); for (String s :
		 * localUses) { System.out.println(s); }
		 */
	}
	// endregion
}
