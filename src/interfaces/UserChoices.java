package interfaces;

import java.io.File;
import java.util.List;

public interface UserChoices {
    List<String> getFiles();

    File getRootFolder();

    String getLanguage();

    String getStyle();

    void setController(Controller listener);
}
