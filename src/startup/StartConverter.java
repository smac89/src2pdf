package startup;

import controllers.Mediator;

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

    public static void main(String[] args) {
        Mediator.start();
    }
}