package seers.astvisitortest;

/**
 * Created by juan on 3/16/16.
 */
public class TypedVariable {
    private String name;
    private String type;

    public TypedVariable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, type);
    }
}
