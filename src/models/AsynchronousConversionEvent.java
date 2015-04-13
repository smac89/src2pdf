package models;

import java.util.Observable;

public class AsynchronousConversionEvent<T> {
    private final T data;
    private final Observable source;
    private final ACTION action;

    public AsynchronousConversionEvent(Observable object, ACTION action, T data) {
        this.action = action;
        this.data = data;
        this.source = object;
    }

    /**
     * @return the data associated with this event
     */
    public final T getData() {
        return data;
    }

    /**
     * @return the source of this event
     */
    public final Observable getSource() {
        return source;
    }

    /**
     * @return the action associated with this event
     */
    public ACTION getAction() {
        return action;
    }

    /**
     * Possible actions for this event
     */
    public enum ACTION {
        FILEREAD, CONVERTTOHTML, CLEANHTML, CONVERTHTMLTOPDF, WRITEPDFTOFILE, NONE
    }
}
