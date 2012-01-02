/*
 * 
 *
 */

package peoplegraph;

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
     * @throws IOException 
     */
    // TODO -
    // Dead end page for case when it is a form
    public static void main(String[] args) {
        Properties properties = new Properties();
        FileInputStream is = null;
        
        try {
            is = new FileInputStream( "PeopleGraph.properties" );
            properties.load( is );
        } catch( IOException e ) {
            // ...
        } finally {
            if( null != is ) {
                try {
                    is.close();
                } catch( IOException e ) {
                    /* .... */
                }
            }
        }

        String linkFileName = "3-24.psv";
        Logger theLogger = Main.makeLogger();
        PeopleGraphView theUI = new PeopleGraphView();
        PeopleGraph theGraph = new PeopleGraph(linkFileName, properties, theLogger);
        theUI.setGraph(theGraph);
        theUI.start();
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
