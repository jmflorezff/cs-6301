package test;

public class TestClass {
	private int unusedField1, unusedField2;
	private int usedField1, usedField2;
	private String unusedField3;
	private String unusedEduardo = 0;

	public void func(int varp1, int varp2) {
		int usedVar1 = 0;
		System.out.println(usedField1 + usedVar1);
		int unusedVar2;
		unusedVar2 = usedField2 + 20;
		unusedField3 = "Still unused";

		int actuallyUnusedvar;
	}

	public void m2() {
		int actuallyUnusedvar = 0;
		System.out.println(actuallyUnusedvar + 1);
	}
}
