/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplegraph;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

/**
 *
 * @author al
 */
public class PeopleGraph {

    List<PersonLinks> peopleLinksList = null;
    Graph<String, String> peopleGraph = null;
    Map<String, Paint> vertexPaints =
        LazyMap.<String, Paint>decorate(new HashMap<String, Paint>(),
        new ConstantTransformer(Color.white));
    Map<String, Paint> edgePaints =
        LazyMap.<String, Paint>decorate(new HashMap<String, Paint>(),
        new ConstantTransformer(Color.blue));

    PeopleGraph(String linkFileName) {
        String theFileName = linkFileName;

        List<String[]> fileData = CSVFile.getFileData(theFileName, "\\|");
        Map<String, PersonLinks> peopleLinks = generateMap(fileData);
        peopleLinksList = generateSortedLinks(peopleLinks);
        peopleGraph = generateGraph(peopleLinksList, 1, 100);
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

        for (PersonLinks theLinks : retVal) {
            String theSource = theLinks.getSource();
            int numLinks = theLinks.numLinks();

            if (numLinks > 1) {
                System.out.println(theSource + ":" + Integer.toString(numLinks));
            }
        }

        return retVal;
    }

    /**
     * 
     * @param peopleLinks
     * @param lowerBound - must be less than upper bound and greater than zero
     * @param upperBound
     * @return
     */
    public Graph<String, String> generateGraph(List<PersonLinks> peopleLinks,
            int lowerBound,
            int upperBound) {
        Graph<String, String> g = new DirectedSparseMultigraph<String, String>();
        Set<String> vertexSet = new TreeSet<String>();
        int edgeCounter = 0;

        for (PersonLinks theLinks : peopleLinks) {
            String theSourceBaseName = getURLBasename(theLinks.getSource());
            int numLinks = theLinks.numLinks();

//            if(!(theSourceBaseName.equalsIgnoreCase("University_of_Edinburgh") ||
//                theSourceBaseName.equalsIgnoreCase("Scotland"))){
//                numLinks = 0;              
//            }

            if (numLinks > lowerBound &&
                    numLinks < upperBound) {
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

        return g;
    }

    String getURLBasename(String theUrl) {
        String[] theComponents = theUrl.split("/");
        String theBasename = theComponents[theComponents.length - 1];
        return theBasename;
    }
    
    public void recolor(AggregateLayout<String, String> layout,
            int numEdgesToRemove,
            Color[] colors) {
        // TODO - want to use the slider values to filter the nodes that are outside
        // the range (number of edges in)
        // in the first instance try to recolour the vertices and incoming edges
        //Now cluster the vertices by removing the top 50 edges with highest betweenness
        //		if (numEdgesToRemove == 0) {
        //			colorCluster( g.getVertices(), colors[0] );
        //		} else {

        Graph<String, String> g = layout.getGraph();
        layout.removeAll();

//        EdgeBetweennessClusterer<Number, Number> clusterer =
//                new EdgeBetweennessClusterer<Number, Number>(numEdgesToRemove);
//        Set<Set<Number>> clusterSet = clusterer.transform(g);
//        List<Number> edges = clusterer.getEdgesRemoved();

        int i = 0;
        //Set the colors of each node so that each cluster's vertices have the same color
//        for (Iterator<Set<Number>> cIt = clusterSet.iterator(); cIt.hasNext();) {
//
//            Set<Number> vertices = cIt.next();
//            Color c = colors[i % colors.length];
//
//            colorCluster(vertices, c);
//            if (groupClusters == true) {
//                groupCluster(layout, vertices);
//            }
//            i++;
//        }
//        for (Number e : g.getEdges()) {
//
//            if (edges.contains(e)) {
//                edgePaints.put(e, Color.lightGray);
//            } else {
//                edgePaints.put(e, Color.black);
//            }
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PeopleGraph sgv = new PeopleGraph("uoe.psv"); // Creates the graph...

        // Layout<V, E>, VisualizationComponent<V,E>
        Layout<String, String> layout =
        	new AggregateLayout<String,String>(new FRLayout<String,String>(sgv.peopleGraph));
        layout.setSize(new Dimension(600, 600));
        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(650, 650));
        // Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<String, Paint>getInstance(sgv.vertexPaints));
//      Edge names add no value here  vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        // Add the mouses mode key listener to work it needs to be added to the visualization component
        vv.addKeyListener(gm.getModeKeyListener());
        
        final JSlider edgeBetweennessSlider = new JSlider(JSlider.HORIZONTAL);
        edgeBetweennessSlider.setBackground(Color.WHITE);
        edgeBetweennessSlider.setPreferredSize(new Dimension(210, 50));
        edgeBetweennessSlider.setPaintTicks(true);
        edgeBetweennessSlider.setMaximum(sgv.peopleGraph.getEdgeCount());
        edgeBetweennessSlider.setMinimum(0);
        edgeBetweennessSlider.setValue(0);
        edgeBetweennessSlider.setMajorTickSpacing(10);
        edgeBetweennessSlider.setPaintLabels(true);
        edgeBetweennessSlider.setPaintTicks(true);

//        	g.removeEdge(e);
//	g.removeVertex(v1);

//        edgeBetweennessSlider.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				JSlider source = (JSlider) e.getSource();
//				if (!source.getValueIsAdjusting()) {
//					int numEdgesToRemove = source.getValue();
//					clusterAndRecolor(layout, numEdgesToRemove, similarColors,
//							groupVertices.isSelected());
//					sliderBorder.setTitle(
//						COMMANDSTRING + edgeBetweennessSlider.getValue());
//					eastControls.repaint();
//					vv.validate();
//					vv.repaint();
//				}
//			}
//		});
        
        final JPanel eastControls = new JPanel();
        eastControls.setOpaque(true);
        eastControls.setLayout(new BoxLayout(eastControls, BoxLayout.Y_AXIS));
        eastControls.add(Box.createVerticalGlue());
        eastControls.add(edgeBetweennessSlider);
        
        final String COMMANDSTRING = "Edges removed for clusters: ";
        final String eastSize = COMMANDSTRING + edgeBetweennessSlider.getValue();

        final TitledBorder sliderBorder = BorderFactory.createTitledBorder(eastSize);
        eastControls.setBorder(sliderBorder);
        //eastControls.add(eastSize);
        eastControls.add(Box.createVerticalGlue());
        
        JPanel south = new JPanel();
        JPanel grid = new JPanel(new GridLayout(2,1));
//        grid.add(scramble);
//        grid.add(groupVertices);
        south.add(grid);
        south.add(eastControls);
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        p.add(gm.getModeComboBox());
        south.add(p);
        
        JFrame frame = new JFrame("Famous Scots Links");
        
        Container content = frame.getContentPane();
        content.add(new GraphZoomScrollPane(vv));
        content.add(south, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
