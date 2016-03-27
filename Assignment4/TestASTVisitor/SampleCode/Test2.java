
public class Test2 {
	private String field1;
	private String field2unused;

	public void method1(int var1unused, int var2used, int var3used) {
		int var3unused = 0;
		int var4unused = 10;
		var3unused = var2used + var3used;
	}

	public void method2() {
		field2unused = "232";
		field1 = " " + field1;
	}
}
