/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplegraph;

import java.util.Comparator;

/**
 *
 * @author al
 */
public class LinkNumberComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        PersonLinks p1 = (PersonLinks) o1;
        PersonLinks p2 = (PersonLinks) o2;

        return p1.compareTo(p2);
    }
}
