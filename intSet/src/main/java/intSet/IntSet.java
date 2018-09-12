/**
 * Representation of a finite set of integers.
 * 
 * @invariant getCount() >= 0
 * @invariant getCount() <= getCapacity()
 */
public class IntSet {

	
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
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Test whether the set is empty.
	 * 
	 * @return getCount() == 0
	 */
	public boolean isEmpty() {
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Test whether a value is in the set
	 * 
	 * @return exists int v in getArray() such that v == value
	 */
	public boolean has(int value) {
		throw new UnsupportedOperationException ("not yet implemented") ;
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
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Removes a value from the set.
	 * 
	 * @post !has(value)
	 * @post this@pre.has(value) implies (getCount() == this@pre.getCount() - 1)
	 * @post !this@pre.has(value) implies (getCount() == this@pre.getCount())
	 */
	public void remove(int value) {
		throw new UnsupportedOperationException ("not yet implemented") ;
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
		throw new UnsupportedOperationException ("not yet implemented") ;
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
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Returns a representation of this set as an array
	 * 
	 * @post return.length == getCount()
	 * @post forall int v in return: has(v)
	 */
	public int[] getArray() {
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Returns the number of elements in the set.
	 */
	public int getCount() {
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Returns the maximal number of elements in the set.
	 */
	public int getCapacity() {
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

	/**
	 * Returns a string representation of the set. The empty set is represented
	 * as {}, a singleton set as {x}, a set with more than one element like {x,
	 * y, z}.
	 */
	public String toString() {
		throw new UnsupportedOperationException ("not yet implemented") ;
	}

}
