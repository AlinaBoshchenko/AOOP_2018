package intSet;

import intSet.IntSet;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

public class IntSetTest {
    private IntSet set;

    @Test
    public void testIsEmpty() {
        set = new IntSet(0);
        assertEquals(set.isEmpty(),true);
    }

    @Test
    public void testHas() {
        set = new IntSet(5);
        set.add(1);
        set.add(5);
        set.add(7);
        set.add(13);
        assertEquals(set.has(1),true);
        assertEquals(set.has(13),true);
        assertEquals(set.has(4),false);
    }

    @Test
    public void testAdd() {
        set = new IntSet(10);
        set.add(2);
        assertEquals(set.has(1),false);
        set.add(1);
        assertEquals(set.has(1),true);

    }

    @Test
    public void testRemove() {
        set = new IntSet(10);
        set.add(2);
        set.add(3);
        assertEquals(set.has(3),true);
        set.remove(3);
        assertEquals(set.has(3),false);
    }

    @Test
    public void testIntersect() {
        fail("Not yet implemented");
    }

    @Test
    public void testUnion() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetArray() {
        fail("Not yet implemented") ;
    }

    @Test
    public void testGetCount() {
        set = new IntSet(10);
        set.add(1);
        set.add(2);
        assertEquals(set.getCount(),2);
    }

    @Test
    public void testGetCapacity() {
        set = new IntSet(25);
        assertEquals(set.getCapacity(),25);
    }

    @Test
    public void testToString() {
        set = new IntSet(10);
        set.add(1);
        set.add(5);
        set.add(8);
        set.add(4);
        set.add(2);
        assertEquals(set.toString(),"{1, 5, 8, 4, 2}");
    }
}
