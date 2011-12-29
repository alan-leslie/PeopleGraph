/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplegraph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author al
 */
public class PersonLinks {
    final String thisLink;
    List<String> otherLinks;
    
    PersonLinks(String thisLink){
        this.thisLink = thisLink;
        otherLinks = new ArrayList<String>();
    }
    
    void addLink(String otherLink){
        otherLinks.add(otherLink);
    }
    
    int compareTo(PersonLinks theOther){
        int thisNumLinks = numLinks();
        int otherNumLinks = theOther.numLinks();
        
        if(thisNumLinks > otherNumLinks)
            return 1;
        else if(thisNumLinks < otherNumLinks)
            return -1;
        else
            return 0;   
    }

    int numLinks() {
        return otherLinks.size();
    }
    
    String getSource(){
        return thisLink;
    }
    
    String getLinkAt(int index){
        if(index >= 0 && index < otherLinks.size()){
           return otherLinks.get(index) ;
        } else {
            return "";
        }
    }
}
