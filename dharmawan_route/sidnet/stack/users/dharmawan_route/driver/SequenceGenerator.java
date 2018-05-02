/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.driver;

/**
 *
 * @author invictus
 */
public class SequenceGenerator {
    private long sequenceNumber;

    public SequenceGenerator() {
        this.sequenceNumber = (long)0;
    }

    public long getandincrement() {
        long x = sequenceNumber;
        sequenceNumber++;
        //System.out.println("Last sequence is:" + x);
        return x;
    }
}