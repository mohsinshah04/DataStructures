import edu.uwm.cs.junit.LockedTestCase;


public class UnlockTest {
	public static void main(String[] args){
		unlock("TestCard","TestCard");
		unlock("TestInvariant","edu.uwm.cs351.Card$TestInvariant");
	}

	private static void unlock(String classname, String sourceName){
		System.out.println("Unlocking tests used in " + classname + ".java");
		LockedTestCase.unlockAll(classname,sourceName);
		System.out.format("Tests in %s.java are unlocked.%n"
				+ "You can run it against your progam now.%n"
				+ "Remember to push %s.tst (refresh the project to show it).%n%n", classname, classname);
	}
}
