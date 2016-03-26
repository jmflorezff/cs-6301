package seers.astvisitortest;

/**
 * Created by juan on 3/16/16.
 */
public class Field extends TypedVariable {
    public Field(String name, String type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return "V: " + super.toString();
    }
}
