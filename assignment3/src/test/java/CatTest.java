import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.junit.Assert.*;

public class CatTest {
    private static BoxMap boxMap;
    private static Box testBox1, testBox2;
    private static Cat cat1, cat2;

    @BeforeClass
    public static void init() {
        boxMap = new BoxMap();
        Address adr1 = new Address("127.0.0.1", 8990),
                adr2 = new Address("127.0.0.1", 8991);
        testBox1 = new Box(5, boxMap, adr1);
        testBox2 = new Box(5, boxMap, adr2);
        new Thread(testBox1).start();
        new Thread(testBox2).start();
        boxMap.getBoxAddresses().add(adr1);
        boxMap.getBoxAddresses().add(adr2);
        cat1 = new Cat();
        cat2 = new Cat();
    }

    @Before
    public void clean() {
        testBox1.killAllCats();
        testBox1.removeDeadCats();
        testBox2.killAllCats();
        testBox2.removeDeadCats();
        cat1.setBox(testBox1);
        cat2.setBox(testBox2);
        testBox1.getCats().add(cat1);
        testBox2.getCats().add(cat2);
    }

    @Test
    public void testPurr() {
        Runnable runTest = () -> cat1.purr();
        Thread testThread = new Thread(runTest);
        testThread.start();
        try {
            Thread.sleep(300); //give the other tread the required time to initialize sleeping
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(testThread.getState(), Thread.State.TIMED_WAITING);
    }

    @Test
    public void testTeleport() {
        cat1.teleport();
        assertEquals(testBox2.getCats().size(), 2); //after teleportation, 2 cats in the second box
        assertEquals(testBox1.getNrCats(), 0); //and 0 in the first
    }

    @Test
    public void testClone() {
        assertEquals(testBox1.getNrCats(), 1);
        cat1.replicate();
        assertEquals(testBox1.getNrCats(), 2);
        cat1.replicate();
        cat1.replicate();
        cat1.replicate();
        assertEquals(testBox1.getNrCats(), 5);
        cat1.replicate();
        assertEquals(testBox1.getNrCats(), 5);
    }

}