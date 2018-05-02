
/*
     Queue.java: create a Queue using the java Vector class.

     This is easy code to write because a Queue has a subset
     of the functionality of the Java Vector class.
     MAH 10/19/01
*/

package sidnet.core.misc;

import java.util.*;  // this contains  the Vector class

public class Queue {

    private Vector queue;

    // constructor
    public Queue ()  {
        queue = new Vector();
    }

    // constructor
    public Queue (int initialSize) {
         if (initialSize >= 1) {
             queue = new Vector(initialSize);
         } else {
             queue = new Vector();
         }
     }

    public void enQueue (Object item) {
        queue.addElement(item);
    }

    public Object front () {
        return queue.firstElement();   
    }

 // I decided to make removing an object on a null queue
 // return a null queue.  I could have thrown an exception instead.

    public Object deQueue () {
        Object obj = null;

       if (!queue.isEmpty()) {
            obj = front();
           queue.removeElementAt(0);
       }

        /* an alternative
        if (! queue.isEmpty()) {
            obj = front();
            queue.removeElementAt(0);
       }
       */
        return obj;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
         return queue.size();
     }

     public int availableRoom() {
         return (queue.capacity() - queue.size());
     }

    // perform a deQueue and convert the result to an int
     public int deQueueString() {
         Object obj = deQueue();
         if (obj != null) {
             return Integer.parseInt(obj.toString());
         } else {
             return  -1;
         }
     }

     // This shows how to use the elements() method
     // print the queue without dequeueing anything
     // Note that it needs to be made smarter to print
     // out complex objects properly.
     public void printQueue() {

         Enumeration e = queue.elements();
         String str;
         while (e.hasMoreElements()) {
             str = e.nextElement().toString();
             System.out.print(str + " ");
         }
         System.out.println();
     }
}
