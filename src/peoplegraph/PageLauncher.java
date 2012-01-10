package peoplegraph;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 *
 * @author al
 */
public class PageLauncher {

    private PageLauncher() {
        Display display = new Display();
    }

    public boolean launch(String theURL) {
        // check it is a valid url
        // make sure starts with http
        boolean isLaunched = Program.launch(theURL);
        return isLaunched;
    }

    public static PageLauncher getInstance() {
        return PageLauncherHolder.INSTANCE;
    }

    private static class PageLauncherHolder {

        private static final PageLauncher INSTANCE = new PageLauncher();
    }
}
