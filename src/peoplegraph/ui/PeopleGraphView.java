package peoplegraph.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

import org.apache.commons.collections15.Transformer;
//import org.apache.commons.collections15.functors.MapTransformer;

import peoplegraph.PageLauncher;
import peoplegraph.PeopleGraph;
import slider.RangeSlider;

/**
 *
 * @author al
 */
public class PeopleGraphView extends JFrame {

    PeopleGraph theGraph = null;
    AggregateLayout<String, String> layout = null;
    VisualizationViewer<String, String> vv = null;
    private SeedFillColor<String> seedFillColor = null;
    private SeedDrawColor<String> seedDrawColor = null;

    public PeopleGraphView() {
        super("Famous Scots Links");
    }

    public void setGraph(PeopleGraph theGraph) {
        this.theGraph = theGraph;
    }

    private void setupRangeControls(JPanel eastControls) {
        eastControls.setOpaque(true);
        eastControls.setLayout(new GridBagLayout());

        JLabel rangeSliderLabel1 = new JLabel();
        final JLabel rangeSliderValue1 = new JLabel();
        JLabel rangeSliderLabel2 = new JLabel();
        final JLabel rangeSliderValue2 = new JLabel();
        final RangeSlider rangeSlider = new RangeSlider();

        rangeSliderLabel1.setText("Lower:");
        rangeSliderLabel2.setText("Upper:");
        rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
        rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);

        rangeSlider.setPreferredSize(new Dimension(240, rangeSlider.getPreferredSize().height));
        rangeSlider.setMinimum(1);
        rangeSlider.setMaximum(theGraph.getMaxNumLinks());

        rangeSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                RangeSlider slider = (RangeSlider) e.getSource();
                rangeSliderValue1.setText(String.valueOf(slider.getValue()));
                rangeSliderValue2.setText(String.valueOf(slider.getUpperValue()));
            }
        });

        JButton scramble = new JButton("Recalculate");
        scramble.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                layout.removeAll();
                theGraph.generateGraph(rangeSlider.getValue(), rangeSlider.getUpperValue());
                layout.setGraph(theGraph.getGraph());
                vv.validate();
                vv.repaint();
            }
        });

        eastControls.add(Box.createVerticalGlue());
        eastControls.add(rangeSliderLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        eastControls.add(rangeSliderValue1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
        eastControls.add(rangeSliderLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        eastControls.add(rangeSliderValue2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
        eastControls.add(rangeSlider, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        eastControls.add(scramble, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        final TitledBorder sliderBorder = BorderFactory.createTitledBorder("Characteristics");
        eastControls.setBorder(sliderBorder);
        eastControls.add(Box.createVerticalGlue());

        rangeSlider.setValue(theGraph.initialRangeLower());
        rangeSlider.setUpperValue(theGraph.initialRangeUpper());

        // Initialize value display.
        rangeSliderValue1.setText(String.valueOf(rangeSlider.getValue()));
        rangeSliderValue2.setText(String.valueOf(rangeSlider.getUpperValue()));
    }

    private void setupGraphView(Container content) {
        // Layout<V, E>, VisualizationComponent<V,E>
        FRLayout<String, String> frLayout = new FRLayout<String, String>(theGraph.getGraph());
        frLayout.setAttractionMultiplier(0.75);
        frLayout.setRepulsionMultiplier(0.85);
        
        layout = new AggregateLayout<String, String>(frLayout);
        layout.setSize(new Dimension(1500, 1100));
        vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(750, 550));
        vv.setDoubleBuffered(true);

        // Show vertex and edge labels
        // Vertex labels unusable when there are many vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        PickedState<String> picked_state = vv.getPickedVertexState();
        seedFillColor = new SeedFillColor<String>(picked_state);
        seedDrawColor = new SeedDrawColor<String>(picked_state);
        //      Edge names add no value here  vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(seedFillColor);
        vv.getRenderContext().setVertexDrawPaintTransformer(seedDrawColor);

        Transformer<String, String> theTips = new ToolTips<String>();
        vv.setVertexToolTipTransformer(theTips);
        vv.setToolTipText("<html><center>Use the mouse wheel to zoom<p>Click and Drag the mouse to pan<p>Shift-click and Drag to Rotate</center></html>");

        String vertex = theGraph.getMostPopular();
        centerOnVertex(vertex);

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
    }

    public void start() {

        final JPanel eastControls = new JPanel();
        setupRangeControls(eastControls);

        JPanel south = new JPanel();
        JPanel grid = new JPanel(new GridLayout(2, 1));

        south.add(grid);
        south.add(eastControls);

        Container content = getContentPane();

        JPanel p = new JPanel();
        setupGraphView(content);

        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        gm.add(new MyPopupGraphMousePlugin());
        vv.setGraphMouse(gm);
        // Add the mouses mode key listener to work it needs to be added to the visualization component
        vv.addKeyListener(gm.getModeKeyListener());

        p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        p.add(gm.getModeComboBox());

        south.add(p);
        content.add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }
    
    private void centerOnPoint(Point2D at) {
        MutableTransformer layoutTrans = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
        MutableTransformer viewTrans = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);

        Point2D ctr = vv.getCenter();
        Point2D pnt = viewTrans.inverseTransform(ctr);

        double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();

        double deltaX = (ctr.getX() - at.getX()) * 1 / scale;
        double deltaY = (ctr.getY() - at.getY()) * 1 / scale;
        Point2D delta = new Point2D.Double(deltaX, deltaY);

        layoutTrans.translate(deltaX, deltaY);
    }

    private void centerOnVertex(String vertex) {
        Point2D at = layout.transform(vertex);
        centerOnPoint(at);
    }
    
    private void goToPage(String vertex) {
        PageLauncher.getInstance().launch(vertex);
    }

    public class ToolTips<E>
            implements Transformer<String, String> {

        @Override
        public String transform(String vertex) {
            int inDegree = theGraph.getGraph().inDegree(vertex);
            int outDegree = theGraph.getGraph().outDegree(vertex);
            String theBaseName = PeopleGraph.getURLBasename(vertex);

            return theBaseName + "-in:" + Integer.toString(inDegree) + " out:" + Integer.toString(outDegree);
        }
    }

    /**
     * a GraphMousePlugin that offers popup
     * menu support
     */
    protected class MyPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin
            implements MouseListener {

        public MyPopupGraphMousePlugin() {
            this(MouseEvent.BUTTON3_MASK);
        }

        public MyPopupGraphMousePlugin(int modifiers) {
            super(modifiers);
        }

        /**
         * If this event is over a node, pop up a menu to
         * allow the user to center view to the node
         *
         * @param e
         */
        @Override
        protected void handlePopup(MouseEvent e) {
            final VisualizationViewer<String, String> vv =
                    (VisualizationViewer<String, String>) e.getSource();
            final Point2D p = e.getPoint();

            GraphElementAccessor<String, String> pickSupport = vv.getPickSupport();
            if (pickSupport != null) {
                final String station = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                if (station != null) {
                    JPopupMenu popup = new JPopupMenu();

                    String center = "Center to Node";

                    popup.add(new AbstractAction("<html><center>" + center) {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            centerOnPoint(p);
                        }
                    });

                    String goToPage = "Open in Browser";

                    popup.add(new AbstractAction("<html><center>" + goToPage) {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            goToPage(station);
                        }
                    });
                    
                    popup.show(vv, e.getX(), e.getY());
                } else {
                }
            }
        }
    }

    private final class SeedDrawColor<V> implements Transformer<V, Paint> {
        protected PickedInfo<V> pi;

        public SeedDrawColor(PickedInfo<V> pi) {
            this.pi = pi;
        }

        @Override
        public Paint transform(V v) {
            return Color.BLACK;
        }
    }

    private final class SeedFillColor<V> implements Transformer<V, Paint> {
        protected PickedInfo<V> pi;

        public SeedFillColor(PickedInfo<V> pi) {
            this.pi = pi;
        }

        @Override
        public Paint transform(V v) {
            if (pi.isPicked(v)) {
                return Color.YELLOW;
            } else {
                String vertex = (String)v;
                int inDegree = theGraph.getGraph().inDegree(vertex);
                int outDegree = theGraph.getGraph().outDegree(vertex);
                
                if(inDegree > 0){
                    if(outDegree > 0){
                        return Color.BLUE;                        
                    } else {
                        return Color.RED;                        
                    }              
                }

                return Color.GREEN;
            }
        }
    }
}
