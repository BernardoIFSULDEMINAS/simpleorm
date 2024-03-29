// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
// SPDX-License-Identifier: CC-BY-SA-3.0
// Original link: https://codereview.stackexchange.com/questions/32991/how-is-my-implementation-of-an-immutable-stack
// Original by "user2894168" on stackexchange
// Changes were made: the additions of the methods equals, toString, reverseAndFilter, reverseInner and getLast. The method reverse was modified as well

package simpleorm;
import java.util.function.Predicate;
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
                        if((this.head == null) != (other.head == null)) return false;
                        if((this.tail == null) != (other.tail == null)) return false;
			return this.head.equals(other.head) && this.tail.equals(other.tail);
		} catch(ClassCastException e) {
			return false;
		}
	}
	
	@Override public String toString() {
		return (head != null ? head.toString() : "head vazia") + ", " + (tail != null ? tail.toString() : "fim");
	}
    public ImStack<T> reverse() {
        return this.reverseInner(new ImStack<T>(), x -> true);
    }
    public ImStack<T> reverseAndFilter(Predicate<T> p) {
        return this.reverseInner(new ImStack<T>(), p);
    }
    private ImStack<T> reverseInner(ImStack<T> acc, Predicate<T> p) {
        if(isEmpty()) {
            return acc;
        }
        if(p.test(head)) {
            return tail.reverseInner(acc.push(head), p);
        } else {
            return tail.reverseInner(acc, p);
        }
    }
    public boolean isEmpty() {
        return head == null && tail == null;
    }
    T getLast() {
        if(tail == null) {
            return head;
        }
        if(tail.head == null) {
            return head;
        }
        return tail.getLast();
    }
}
