package peoplegraph;

import java.util.logging.Level;
import peoplegraph.ui.PeopleGraphView;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author al
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    // TODO - thread generate graph
    // only draw graph after layout adjustments are complete
    // set size and zoom depending on number of vertices
    // play with FRLayout setting to clarify large number of vertices
    public static void main(String[] args) {
        Properties properties = new Properties();
        FileInputStream is = null;

        try {
            is = new FileInputStream("PeopleGraph.properties");
            properties.load(is);
        } catch (IOException e) {
            // ...
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    /* .... */
                }
            }
        }

        Logger theLogger = Main.makeLogger();
        
        try {
            PeopleGraph theGraph = new PeopleGraph(properties, theLogger);
            PeopleGraphView theUI = new PeopleGraphView();
            theUI.setGraph(theGraph);
            theUI.start();
        } catch (IOException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return - valid logger (single file).
     */
    private static Logger makeLogger() {
        Logger lgr = Logger.getLogger("PeopleGraph");
        lgr.setUseParentHandlers(false);
        lgr.addHandler(simpleFileHandler());
        return lgr;
    }

    /**
     *
     * @return - valid file handler for logger.
     */
    private static FileHandler simpleFileHandler() {
        try {
            FileHandler hdlr = new FileHandler("PeopleGraph.log");
            hdlr.setFormatter(new SimpleFormatter());
            return hdlr;
        } catch (Exception e) {
            System.out.println("Failed to create log file");
            return null;
        }
    }
}
