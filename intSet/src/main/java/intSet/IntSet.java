package intSet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Representation of a finite set of integers.
 *
 * @invariant getCount() >= 0
 * @invariant getCount() <= getCapacity()
 */
public class IntSet {
	private ArrayList<Integer>elements;
	private int capacity;

	/**
	 * Creates a new set with 0 elements.
	 *
	 * @param capacity
	 *            the maximal number of elements this set can have
	 * @pre capacity >= 0
	 * @post getCount() == 0
	 * @post getCapacity() == capacity
	 */
	public IntSet(int capacity) {
		if(capacity < 0) {
			throw new IllegalArgumentException("Capacity should be positive.");
		}
		this.capacity = capacity;
		elements = new ArrayList<>(capacity);
	}

	/**
	 * Creates a new set which contains all the elements of the other set and a explicit capacity.
	 *
	 * @param other
	 * 			  the set from which to copy the intial elements
	 * @param capacity
	 *            the maximal number of elements this set can have
	 * @pre capacity >= 0 && other != null && other.getCount() <= capacity
	 * @post getCount() == 0
	 * @post getCapacity() == capacity
	 */
	public IntSet(IntSet other, int capacity) {
		if(capacity < 0) {
			throw new IllegalArgumentException("Capacity should be positive.");
		}
		if(other == null) {
			throw new IllegalArgumentException("The other set should not be null.");
		}
		if(other.getCount() > capacity) {
			throw new RuntimeException("The source set should have a smaller cardinality" +
					"than the capacity of the destination set.");
		}
		this.capacity = capacity;
		elements = new ArrayList<>(other.getElements());
	}

	/**
	 * Creates a new set which duplicates the other set
	 *
	 * @param other
	 * 			  the set which to duplicate
	 * @pre other != null
	 * @post getCount() == 0
	 * @post getCapacity() == capacity
	 */
	public IntSet(IntSet other) {
		this(other, other.getCapacity());
	}

	/**
	 * Test whether the set is empty.
	 *
	 * @return getCount() == 0
	 */
	public boolean isEmpty() {
		return (getCount() == 0);
	}

	/**
	 * Test whether a value is in the set
	 *
	 * @return exists int v in getArray() such that v == value
	 */
	public boolean has(int value) {
		return elements.contains(value);
	}

	/**
	 * Adds a value to the set.
	 *
	 * @pre getCount() < getCapacity()
	 * @post has(value)
	 * @post !this@pre.has(value) implies (getCount() == this@pre.getCount() + 1)
	 * @post this@pre.has(value) implies (getCount() == this@pre.getCount())
	 */
	public void add(int value) {
		if(getCount() >= getCapacity()) {
			throw new RuntimeException("Capacity of the set exceeded");
		}
		if(!has(value)) {
			elements.add(value);
		}
	}

	/**
	 * Removes a value from the set.
	 *
	 * @post !has(value)
	 * @post this@pre.has(value) implies (getCount() == this@pre.getCount() - 1)
	 * @post !this@pre.has(value) implies (getCount() == this@pre.getCount())
	 */
	public void remove(int value) {
		elements.remove(new Integer(value));
	}

	/**
	 * Returns the maximum capacity between two sets
	 */
	private static int maxCapacity(IntSet first, IntSet second) {
		if(first == null || second == null) {
			throw new IllegalArgumentException("Sets should not be null");
		}
		return (first.getCapacity() > second.getCapacity()) ? first.getCapacity() : second.getCapacity();
	}


	/**
	 * Returns the intersection of this set and another set.
	 *
	 * @param other
	 *            the set to intersect this set with
	 * @return the intersection
	 * @pre other != null
	 * @post forall int v: (has(v) and other.has(v)) implies return.has(v)
	 * @post forall int v: return.has(v) implies (has(v) and other.has(v))
	 */
	public IntSet intersect(IntSet other) {
		if(other == null) {
			throw new IllegalArgumentException("The other set should not be null");
		}
		IntSet intersectSet = new IntSet(maxCapacity(this, other));
		for(Integer element : elements) {
			if(other.has(element)) {
				intersectSet.add(element);
			}
		}
		return intersectSet;
	}

	/**
	 * Returns the union of this set and another set.
	 *
	 * @param other
	 *            the set to union this set with
	 * @return the union
	 * @pre other != null
	 * @post forall int v: has(v) implies return.has(v)
	 * @post forall int v: other.has(v) implies return.has(v)
	 * @post forall int v: return.has(v) implies (has(v) or other.has(v))
	 */
	public IntSet union(IntSet other) {
		if(other == null) {
			throw new IllegalArgumentException("The other set should not be null");
		}
		IntSet unionSet = new IntSet(getCapacity() + other.getCapacity());
		for(Integer element : elements) {
			unionSet.add(element);
		}
		for(Integer element : other.elements) {
			unionSet.add(element);
		}
		return unionSet;
	}

	/**
	 * Returns the difference of this set the other set.
	 *
	 * @param other
	 *            the set to difference this set with
	 * @return the difference
	 * @pre other != null
	 * @post forall int v: return.has(v) implies (has(v) and !other.has(v))
	 */
	public IntSet difference(IntSet other) {
		if(other == null) {
			throw new IllegalArgumentException("The other set should not be null");
		}
		IntSet differenceSet = new IntSet(this);
		for(Integer element : other.getElements()) {
			differenceSet.remove(element);
		}
		return differenceSet;

	}

	/**
	 * Returns the symmetric difference of this set the other set.
	 *
	 * @param other
	 *            the set to difference this set with
	 * @return the symmetric difference
	 * @pre other != null
	 * @post forall int v: return.has(v) implies ((has(v) or other.has(v)) && !(has(v) && other.has(v))
	 */
	public IntSet symmetricDifference(IntSet other) {
		return union(other).difference(intersect(other));
	}

	/**
	 * Returns a representation of this set as an array
	 *
	 * @post return.length == getCount()
	 * @post forall int v in return: has(v)
	 */
	public int[] getArray() {
		int[] array = new int[elements.size()];
		Iterator<Integer> iterator = elements.iterator();
		for(int i = 0; i < array.length; ++i) {
			array[i] = iterator.next();
		}
		return array;
	}

	/**
	 * Returns the elements of this set as an ArrayList
	 */
	public ArrayList<Integer> getElements() {
		return elements;
	}
	/**
	 * Returns the number of elements in the set.
	 */
	public int getCount() {
		return elements.size();
	}


	/**
	 * Returns the maximal number of elements in the set.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns a string representation of the set. The empty set is represented
	 * as {}, a singleton set as {x}, a set with more than one element like {x,
	 * y, z}.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder("{");
		Iterator<Integer> iterator = elements.iterator();
		if(iterator.hasNext()) {
			buffer.append(iterator.next());
		}
		while(iterator.hasNext()) {
			buffer.append(", ").append(iterator.next());
		}
		return buffer.append('}').toString();
	}

}