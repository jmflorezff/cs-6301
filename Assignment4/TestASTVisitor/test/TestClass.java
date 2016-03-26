package test;

public class TestClass {
	private int unusedField1, unusedField2;
	private int usedField1, usedField2;
	private String unusedField3;
	private String unusedEduardo = 0;

	public void func(int p1, int p2) {
		int usedVar1 = 0;
		System.out.println(usedField1 + usedVar1);
		int unusedVar2;
		unusedVar2 = usedField2 + 20;
		unusedField3 = "Still unused";
		Integer unused = 0;

		unusedField3 = unusedField1 + unusedField2 + unusedField3;

		int actuallyUnused;
	}

	public void m2() {
		int actuallyUnused = 0;
		System.out.println(actuallyUnused + 1);
	}
}
