package intSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IntSetTest {
    private IntSet set;
    private IntSet tempSet;

    public void setUp(int capacity){
        set = new IntSet(capacity);
    }

    public void setUpTemp(int capacity){
        tempSet = new IntSet(capacity);
    }


    //Uncomment if all test methods setUp an IntSet of capacity 5
    /*
    @Before
    public void setUp() {
        set = new IntSet(5);
    }*/

    @Test(expected = IllegalArgumentException.class)
    public void testFirstConstructor() {
        new IntSet(-1); //create a set with invalid capacity
    }

    @Test(expected = RuntimeException.class)
    public void testSecondConstructor() {
        setUpTemp(3);
        tempSet.add(1);
        tempSet.add(2);
        tempSet.add(3);
        new IntSet(tempSet, 2);
    }

    @Test(expected = NullPointerException.class)
    public void testThirdConstructor() {
        IntSet nullSet = null;
        new IntSet(nullSet);
    }

    @Test
    public void testIsEmpty() {
        setUp(1);
        assertEquals(set.isEmpty(),true);
        set.add(2);
        assertEquals(set.isEmpty(),false);
    }

    @Test
    public void testHas() {
        setUp(4);
        set.add(1);
        set.add(5);
        set.add(7);
        set.add(7);
        assertEquals(set.has(7),true);
        set.remove(7);
        assertEquals(set.has(13),false);
        set.add(13);
        assertEquals(set.has(7),false);
        assertEquals(set.has(1),true);
        assertEquals(set.has(13),true);
        assertEquals(set.has(4),false);
    }

    @Test
    public void testAdd() {
        setUp(2);
        set.add(2);
        assertEquals(set.has(1),false);
        assertEquals(set.has(2), true);
        set.add(1);
        assertEquals(set.has(1),true);
        set.add(2); //should not throw runtime exception
        set.add(1); //should not throw runtime exception

    }

    @Test
    public void testRemove() {
        setUp(2);
        set.add(2);
        set.add(3);
        assertTrue(set.has(3));
        set.remove(3);
        set.remove(3); //this should not throw any exception
        assertFalse(set.isEmpty());
        assertFalse(set.has(3));
        set.remove(2);
        assertFalse(set.has(2));
        assertTrue(set.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveEmptySet() {
        setUp(0);
        set.remove(10); //should throw exception
    }

    @Test
    public void testIntersect() {
        setUp(5);
        setUpTemp(5);
        set = set.intersect(tempSet); //2 empty sets
        assertEquals(set.toString(),"{}");

        set.add(5);
        set.add(7);
        set.add(14);
        set.add(2);
        set.add(21);

        tempSet = set.intersect(tempSet); //'normal' set with empty set
        assertEquals(tempSet.toString(), "{}");

        tempSet.add(21);
        tempSet.add(8);
        tempSet.add(4);
        tempSet.add(7);
        tempSet.add(10);

        set = set.intersect(tempSet); //2 full sets
        assertEquals(set.toString(),"{7, 21}");
    }

    @Test
    public void testUnion() {
        setUp(4);
        setUpTemp(5);
        set = set.union(tempSet); //2 empty sets
        assertEquals(set.toString(),"{}");

        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);

        set = set.union(tempSet); //'normal' set with empty set
        assertEquals(set.toString(), "{1, 2, 3, 4}");

        tempSet.add(4);
        tempSet.add(5);
        tempSet.add(6);
        tempSet.add(7);
        tempSet.add(8);

        set = set.union(tempSet); //2 full sets
        assertEquals(set.toString(),"{1, 2, 3, 4, 5, 6, 7, 8}");
    }

    @Test
    public void testDifference() {
        setUp(5);
        setUpTemp(5);
        set = set.difference(tempSet); //2 empty sets
        assertEquals(set.toString(),"{}");

        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        set = set.difference(tempSet); //'normal' set with empty set
        assertEquals(set.toString(),"{1, 2, 3, 4, 5}");

        tempSet.add(4);
        tempSet.add(5);
        tempSet.add(6);
        tempSet.add(7);
        tempSet.add(8);

        set = set.difference(tempSet); //2 full sets
        assertEquals(set.toString(),"{1, 2, 3}");
    }

    @Test
    public void testSymmetricDifference() {
        setUp(5);
        setUpTemp(5);
        set = set.symmetricDifference(tempSet); //2 empty sets
        assertEquals(set.toString(),"{}");

        set.add(6);
        set.add(15);
        set.add(2);
        set.add(3);
        set.add(9);

        set = set.symmetricDifference(tempSet); //'normal' set with empty set
        assertEquals(set.toString(),"{6, 15, 2, 3, 9}");

        tempSet.add(13);
        tempSet.add(2);
        tempSet.add(5);
        tempSet.add(9);
        tempSet.add(4);

        set = set.symmetricDifference(tempSet); //2 full sets
        assertEquals(set.toString(),"{6, 15, 3, 13, 5, 4}");

        setUp(0);
        setUpTemp(0);
        assertEquals(set.symmetricDifference(tempSet).toString(), "{}");
    }


    @Test(expected = NullPointerException.class)
    public void testSymmetricDifferenceNull() {
        IntSet nullSet = null;
        setUpTemp(2);
        set.symmetricDifference(nullSet);
    }



    @Test
    public void testGetArray() {
        setUp(4);
        set.add(1);
        set.add(6);
        set.add(2);
        set.add(7);

        int setArray[] = set.getArray();
        int tempArray[] = {1,6,2,7};
        for(int i = 0; i < set.getCount(); i++){
            assertEquals(setArray[i],tempArray[i]);
        }
    }

    @Test
    public void testGetElements() {
        setUp(4);
        set.add(1);
        set.add(6);
        set.add(2);
        set.add(7);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(6);
        list.add(2);
        list.add(7);

        assertEquals(set.getElements(),list);
    }

    @Test
    public void testGetCount() {
        setUp(2);
        assertEquals(set.getCount(),0);
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
        assertEquals(set.toString(),"{1, 5, 8}");
        set.add(4);
        set.add(2);
        assertEquals(set.toString(),"{1, 5, 8, 4, 2}");
    }

    @Test
    public void testEquality() {
        setUp(5);
        setUpTemp(4);
        set.add(1);
        set.add(2);
        tempSet.add(1);
        tempSet.add(2);
        assertNotEquals(set, tempSet); //false because have different capacities
        tempSet.setCapacity(5);
        assertEquals(set, tempSet); //now should be true
        assertEquals(set.hashCode(), tempSet.hashCode()); //the hash codes should also coincide
        tempSet.remove(1);
        assertNotEquals(set, tempSet);
        assertNotEquals(set.hashCode(), tempSet.hashCode());

    }
}
