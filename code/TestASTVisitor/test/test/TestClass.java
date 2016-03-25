package test;

/**
 * Created by juan on 3/25/16.
 */
public class TestClass {
    private int unusedField1, unusedField2;
    private int usedField1, usedField2;
    private String unusedField3;

    public void func(int p1, int p2) {
        int usedVar1 = 0;
        System.out.println(usedField1 + usedVar1);
        int unusedVar2;
        unusedVar2 = usedField2 + 20;
        unusedField3 = "Still unused";

        int actuallyUnused;
    }

    public void m2() {
        int actuallyUnused = 0;
        System.out.println(actuallyUnused + 1);
    }

    /*

     File: TestClass.java
     * The [field] [unusedField1] is declared but never read in the code (line:[7])
     * The [field] [unusedField2] is declared but never read in the code (line:[7])
     * The [field] [unusedField3] is declared but never read in the code (line:[9])
     * The [variable] [unusedVar2] is declared but never read in the code (line:[14])
     * The [variable] is declared but never read in the code (line:[18])

    * */
}
