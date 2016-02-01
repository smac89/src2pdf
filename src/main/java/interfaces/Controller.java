package interfaces;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observer;

public interface Controller extends ActionListener, Observer {
    List<String> getStyles();

    List<String> getLanguages();
}