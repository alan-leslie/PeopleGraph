/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

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
       
      public PeopleGraphView(){
           super("Famous Scots Links");
       }
       
    public void setGraph(PeopleGraph theGraph) {
        this.theGraph = theGraph;
    }

    public void start() {
        // Layout<V, E>, VisualizationComponent<V,E>
        final AggregateLayout<String, String> layout =
                new AggregateLayout<String, String>(new FRLayout<String, String>(theGraph.getGraph()));
        layout.setSize(new Dimension(5000, 5000));
        final VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(550, 550));
        vv.setDoubleBuffered(true);
//        Point2D center = vv.getCenter();
//        vv.setLocation((int)center.getX(), (int)center.getY());
        // Show vertex and edge labels
//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<String, Paint>getInstance(theGraph.getVertexPaints()));
//      Edge names add no value here  vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        // Add the mouses mode key listener to work it needs to be added to the visualization component
        vv.addKeyListener(gm.getModeKeyListener());
        Transformer<String, String> theTips = null; //new VoltageTips<Number>();
        vv.setVertexToolTipTransformer(null); //todo
        
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

        final JPanel eastControls = new JPanel();
        eastControls.setOpaque(true);
        eastControls.setLayout(new GridBagLayout());

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

        JPanel south = new JPanel();
        JPanel grid = new JPanel(new GridLayout(2, 1));

        south.add(grid);
        south.add(eastControls);
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        p.add(gm.getModeComboBox());
        south.add(p);

        Container content = getContentPane();
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
        JScrollBar hScroll = scrollPane.getHorizontalScrollBar();
        int value = hScroll.getValue();
//        Rectangle theRect = new Rectangle(1000, 1000, 3000, 3000);
//        scrollPane.scrollRectToVisible(theRect);
        content.add(scrollPane);
        content.add(south, BorderLayout.SOUTH);

        rangeSlider.setValue(theGraph.initialRangeLower());
        rangeSlider.setUpperValue(theGraph.initialRangeUpper());

        // Initialize value display.
        rangeSliderValue1.setText(String.valueOf(rangeSlider.getValue()));
        rangeSliderValue2.setText(String.valueOf(rangeSlider.getUpperValue()));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }   
}
