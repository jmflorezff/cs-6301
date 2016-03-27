package test;

/**
 * Created by juan on 3/22/16.
 */
public class TestClass {
    private int usedField1, usedField2;
    private String unusedField3;
    int[] unusedArray={1,2}, usedArray={1,2};

    public void func(int p1, int p2) {
        int usedVar1 = 0;
        System.out.println(usedField1 + usedVar1);
        int unusedVar2;
        unusedVar2 = usedField2 + 20;
        unusedField3 = "Still unused";

        int actuallyUnusedInFunc;
    }

    public void m2() {
        int actuallyUnusedInFunc = 0;
        System.out.println(actuallyUnusedInFunc + 1);
        int usedIndex=1;
        usedArray[usedIndex]=0;

        TestType usedType=new TestType();
        usedType.me().i = 8;
    }
    private int unusedField1, unusedField2;
}

class TestType{
    public int i = 0;

    class NestedType{
        private int y;
    }

    public TestType me(){
        return this;
    }
}
