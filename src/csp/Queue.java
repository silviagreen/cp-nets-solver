package csp;

public interface Queue<E> extends java.util.Queue<E> {
    /**
     * EMPTY?(queue)
     * 
     * @return true only if there are no elements on the queue.
     */
    boolean isEmpty();

    /**
     * POP(queue)
     * 
     * @return the first element of the queue.
     */
    E pop();

    /**
     * INSERT(element, queue)
     * 
     * @param element
     *            to be inserted in the queue.
     * @return the resulting queue with the element inserted. null is returned
     *         if the element could not be inserted.
     */
    Queue<E> insert(E element);
}
