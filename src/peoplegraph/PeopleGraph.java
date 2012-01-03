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

import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author al
 */
public class PeopleGraph {

    private List<PersonLinks> peopleLinksList = null;
    Graph<String, String> peopleGraph = null;
    private int theMaxNumLinks = 1;
    private String mostPopularVertex = "";

    PeopleGraph(Properties properties,
            Logger logger) {
        String theFileName = properties.getProperty("LinkFileName", "all_peeps.psv");

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

            if (numLinks >= initialRangeLower()) {
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
    public final void generateGraph(int lowerBound,
            int upperBound) {
        Graph<String, String> g = new DirectedSparseMultigraph<String, String>();
        Set<String> vertexSet = new TreeSet<String>();
        int edgeCounter = 0;

        ListIterator<PersonLinks> iter = peopleLinksList.listIterator(peopleLinksList.size());
        mostPopularVertex = "";

        while (iter.hasPrevious()) {
            PersonLinks theLinks = iter.previous();

            String theSourceName = theLinks.getSource();
            int numLinks = theLinks.numLinks();

            if (numLinks >= lowerBound
                    && numLinks <= upperBound) {
                if (!vertexSet.contains(theSourceName)) {
                    g.addVertex(theSourceName);
                    vertexSet.add(theSourceName);
                }
                
                if(mostPopularVertex.isEmpty()){               
                    mostPopularVertex = theSourceName;
                }

                for (int i = 0; i < numLinks; ++i) {
                    String theTargetName = theLinks.getLinkAt(i);

                    if (!vertexSet.contains(theTargetName)) {
                        g.addVertex(theTargetName);
                        vertexSet.add(theTargetName);
                    }

                    String edgeName = "Edge-" + Integer.toString(edgeCounter);
                    ++edgeCounter;
                    g.addEdge(edgeName, theTargetName, theSourceName, EdgeType.DIRECTED);
                }
            }
        }

        this.peopleGraph = g;
    }

    static public String getURLBasename(String theUrl) {
        String[] theComponents = theUrl.split("/");
        String theBasename = theComponents[theComponents.length - 1];
        return theBasename;
    }

    public final int getMaxNumLinks() {
        return theMaxNumLinks;
    }

    public final int initialRangeLower() {
        if (theMaxNumLinks > 30) {
            return (int) (theMaxNumLinks * 0.1);
        }

        return 2;
    }

    public Graph<String, String> getGraph() {
        return peopleGraph;
    }

    public final int initialRangeUpper() {
        if (theMaxNumLinks > 30) {
            return (int) (theMaxNumLinks * 0.9);
        }

        return theMaxNumLinks;
    }

    public String getMostPopular() {
        return mostPopularVertex;
    }
}
