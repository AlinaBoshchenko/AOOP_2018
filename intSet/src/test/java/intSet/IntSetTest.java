package intSet;

import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

public class IntSetTest {
    private IntSet set;

    public void setUp(int capacity){
        set = new IntSet(capacity);
    }

    @Test
    public void testIsEmpty() {
        setUp(0);
        assertEquals(set.isEmpty(),true);
    }

    @Test
    public void testHas() {
        setUp(4);
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
        setUp(2);
        set.add(2);
        assertEquals(set.has(1),false);
        set.add(1);
        assertEquals(set.has(1),true);

    }

    @Test
    public void testRemove() {
        setUp(2);
        set.add(2);
        set.add(3);
        assertEquals(set.has(3),true);
        set.remove(3);
        assertEquals(set.has(3),false);
        set.remove(2);
        assertEquals(set.has(2),false);
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
        setUp(2);
        set.add(1);
        set.add(2);
        assertEquals(set.getCount(),2);
    }

    @Test
    public void testGetCapacity() {
        setUp(25);
        assertEquals(set.getCapacity(),25);
        setUp(0);
        assertEquals(set.getCapacity(),0);
    }

    @Test
    public void testToString() {
        setUp(5);
        set.add(1);
        set.add(5);
        set.add(8);
        set.add(4);
        set.add(2);
        assertEquals(set.toString(),"{1, 5, 8, 4, 2}");
    }
}
