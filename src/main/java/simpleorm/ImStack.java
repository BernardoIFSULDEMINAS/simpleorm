package simpleorm;
class ImStack<T> {

    private final T head;
    private final ImStack<T> tail;

    ImStack(T head, ImStack<T> tail)
    {
        this.head = head;
        this.tail = tail;
    }
	
	ImStack() {
		this.head = null;
		this.tail = null;
	}

    ImStack<T> pop()
    {
        return this.tail;
    }
    ImStack<T> push(T e)
    {
        return new ImStack<T>(e, this);
    }
    T peek()
    {
        return this.head;
    }
	
	@Override public boolean equals(Object o) {
		try {
			if(o == this) {return true;}
			if(!(o instanceof ImStack)) {return false;}
			ImStack<T> other = (ImStack<T>)o;
			if(this.head == null) {return other.head == null;}
			if(this.tail == null) {return other.tail == null;}
			return this.tail.equals(other.tail);
		} catch(ClassCastException e) {
			return false;
		}
	}
}