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
}