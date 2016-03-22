package test;

/**
 * Created by juan on 3/22/16.
 */
public class TestClass {
    private int unusedField1;

    public void fun1(String unusedParam1, String unusedParam2) {
        int usedVar1 = 0;
        unusedParam1="a";
        System.out.print(usedVar1);
    }
}
