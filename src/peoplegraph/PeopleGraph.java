/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.Color;
import java.awt.Paint;

import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

/**
 *
 * @author al
 */
public class PeopleGraph {

    private List<PersonLinks> peopleLinksList = null;
    Graph<String, String> peopleGraph = null;
    Map<String, Paint> vertexPaints =
            LazyMap.<String, Paint>decorate(new HashMap<String, Paint>(),
            new ConstantTransformer(Color.white));
    Map<String, Paint> edgePaints =
            LazyMap.<String, Paint>decorate(new HashMap<String, Paint>(),
            new ConstantTransformer(Color.blue));
    private int theMaxNumLinks = 1;

    PeopleGraph(String linkFileName,
            Properties properties,
            Logger logger) {
        String theFileName = linkFileName;

        List<String[]> fileData = CSVFile.getFileData(theFileName, "\\|");
        Map<String, PersonLinks> peopleLinks = generateMap(fileData);
        peopleLinksList = generateSortedLinks(peopleLinks);
        generateGraph(initialRangeLower(), initialRangeUpper());
    }

    private Map<String, PersonLinks> generateMap(List<String[]> fileData) {
        Map<String, PersonLinks> retVal = new HashMap<String, PersonLinks>();

        for (String[] theLinkPair : fileData) {
            String theSource = theLinkPair[1];
            String theTarget = theLinkPair[0];
            PersonLinks theLinks = null;

            if (retVal.containsKey(theSource)) {
                theLinks = retVal.get(theSource);
                theLinks.addLink(theTarget);
            } else {
                theLinks = new PersonLinks(theSource);
                theLinks.addLink(theTarget);
                retVal.put(theSource, theLinks);
            }
        }

        return retVal;
    }

    private List<PersonLinks> generateSortedLinks(Map<String, PersonLinks> peopleLinks) {
        List<PersonLinks> retVal = new ArrayList<PersonLinks>();

        for (Map.Entry<String, PersonLinks> entry : peopleLinks.entrySet()) {
            retVal.add(entry.getValue());
        }

        Collections.sort(retVal, new LinkNumberComparator());
        theMaxNumLinks = retVal.get(retVal.size() - 1).numLinks();

        for (PersonLinks theLinks : retVal) {
            String theSource = theLinks.getSource();
            int numLinks = theLinks.numLinks();

            if (numLinks > initialRangeLower()) {
                System.out.println(theSource + ":" + Integer.toString(numLinks));
            }
        }

        return retVal;
    }

    /**
     * 
     * @param lowerBound - must be less than upper bound and greater than zero
     * @param upperBound
     */
    public void generateGraph(int lowerBound,
            int upperBound) {
        Graph<String, String> g = new DirectedSparseMultigraph<String, String>();
        Set<String> vertexSet = new TreeSet<String>();
        int edgeCounter = 0;

        ListIterator<PersonLinks> iter = peopleLinksList.listIterator(peopleLinksList.size());

        while (iter.hasPrevious()) {
            PersonLinks theLinks = iter.previous();

            String theSourceBaseName = getURLBasename(theLinks.getSource());
            int numLinks = theLinks.numLinks();

            if (numLinks >= lowerBound
                    && numLinks <= upperBound) {
                if (!vertexSet.contains(theSourceBaseName)) {
                    g.addVertex(theSourceBaseName);
                    vertexSet.add(theSourceBaseName);
                    vertexPaints.put(theSourceBaseName, Color.RED);
                }

                for (int i = 0; i < numLinks; ++i) {
                    String theTargetBasename = getURLBasename(theLinks.getLinkAt(i));

                    if (!vertexSet.contains(theTargetBasename)) {
                        g.addVertex(theTargetBasename);
                        vertexSet.add(theTargetBasename);
                        vertexPaints.put(theTargetBasename, Color.GREEN);
                    }

                    String edgeName = "Edge-" + Integer.toString(edgeCounter);
                    ++edgeCounter;
                    g.addEdge(edgeName, theTargetBasename, theSourceBaseName, EdgeType.DIRECTED);
                }
            }
        }

        this.peopleGraph = g;
    }

    String getURLBasename(String theUrl) {
        String[] theComponents = theUrl.split("/");
        String theBasename = theComponents[theComponents.length - 1];
        return theBasename;
    }

    public int getMaxNumLinks() {
        return theMaxNumLinks;
    }

    public int initialRangeLower() {
        if (theMaxNumLinks > 30) {
            return (int) (theMaxNumLinks * 0.1);
        }

        return 2;
    }


    public Graph<String, String> getGraph() {
        return peopleGraph;
    }
    
    public int initialRangeUpper() {
        if (theMaxNumLinks > 30) {
            return (int) (theMaxNumLinks * 0.9);
        }

        return theMaxNumLinks;
    }
    
    public Map<String, Paint> getVertexPaints(){
        return vertexPaints;
    }
    
    public Map<String, Paint> getEdgePaints(){
        return edgePaints;
    }

 

}

    

