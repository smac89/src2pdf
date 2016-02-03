package startup;

import controllers.Mediator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Initial class that is called when the program starts<br\>
 * This class is responsible for starting the controller, ex:
 * <code> <pre>
 * public static void main StartConverter() {
 * Controller ctrl = ...;
 * ctrl.start();
 * } </pre></code>
 *
 * @author Smac89
 */
public final class StartConverter {

    public static void main(String[] args) throws InterruptedException, ExecutionException, InvocationTargetException {

        Properties p = System.getProperties();
        String source = String.format("%1$ssrc%1$smain%1$sjava%1$s", File.separator);
        p.setProperty("project.sources", p.getProperty("user.dir") + source);

        Mediator.start();
    }

    /**
     * Adds user.dir into python.path to make Jython look for python modules in working directory in all cases
     * (both standalone and not standalone modes)
     *
     * @param props
     * @return props
     */
    private static Properties setDefaultPythonPath(Properties props) {
        String pythonPathProp = props.getProperty("python.path");
        String new_value;
        if (pythonPathProp == null) {
            new_value = System.getProperty("user.dir");
        } else {
            new_value = pythonPathProp + File.pathSeparator + System.getProperty("user.dir") + File.pathSeparator;
        }
        props.setProperty("python.path", new_value);
        return props;
    }
}