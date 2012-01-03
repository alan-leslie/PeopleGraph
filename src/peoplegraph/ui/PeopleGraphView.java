
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

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
    
    private void setupGraphView(Container content){
        // Layout<V, E>, VisualizationComponent<V,E>
        FRLayout<String, String> frLayout = new FRLayout<String, String>(theGraph.getGraph());
        layout = new AggregateLayout<String, String>(frLayout);
        layout.setSize(new Dimension(1000, 1000));
        vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(550, 550));
        vv.setDoubleBuffered(true);

        // Show vertex and edge labels
//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<String, Paint>getInstance(theGraph.getVertexPaints()));
//      Edge names add no value here  vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        // Create a graph mouse and add it to the visualization component

        Transformer<String, String> theTips = new ToolTips<String>();
        vv.setVertexToolTipTransformer(theTips);

        MutableTransformer layoutTrans = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
        MutableTransformer viewTrans = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);

        Point2D ctr = vv.getCenter();
        Point2D pnt = viewTrans.inverseTransform(ctr);

        double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
        Point2D at = layout.transform("University_of_Edinburgh");
        double deltaX = (ctr.getX() - at.getX()) * 1 / scale;
        double deltaY = (ctr.getY() - at.getY()) * 1 / scale;
        Point2D delta = new Point2D.Double(deltaX, deltaY);

        layoutTrans.translate(deltaX, deltaY);
        
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

    public class ToolTips<E>
            implements Transformer<String, String> {

        @Override
        public String transform(String vertex) {
            return vertex;
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

                        public void actionPerformed(ActionEvent e) {

                            MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
                            MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                            Point2D ctr = vv.getCenter();
                            Point2D pnt = view.inverseTransform(ctr);

                            double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();

                            double deltaX = (ctr.getX() - (p.getX())) * 1 / scale;
                            double deltaY = (ctr.getY() - (p.getY())) * 1 / scale;
                            Point2D delta = new Point2D.Double(deltaX, deltaY);

                            layout.translate(deltaX, deltaY);
                        }
                    });

                    popup.show(vv, e.getX(), e.getY());
                } else {
                }
            }
        }
    }
}
