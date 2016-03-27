package seers.astvisitortest;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juan on 3/16/16.
 */
public class Method {
    private String name;
    private List<TypedVariable> parameters;

    public Method(String name, List<SingleVariableDeclaration> parameters) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters.size());
        for (SingleVariableDeclaration parameter : parameters) {
            this.parameters.add(new TypedVariable(parameter.getName().getFullyQualifiedName(),
                    parameter.getType().toString()));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TypedVariable> getParameters() {
        return parameters;
    }

    public void setParameters(List<TypedVariable> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        String[] paramStrings = new String[getParameters().size()];
        int i = 0;
        for (TypedVariable variable : parameters) {
            paramStrings[i++] = variable.toString();
        }

        return String.format("M: %s (%s)", getName(), String.join(", ", paramStrings));
    }
}
