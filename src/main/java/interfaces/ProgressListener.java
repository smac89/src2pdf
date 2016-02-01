package interfaces;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import models.AsynchronousConversionEvent;

import java.util.Observer;

public interface ProgressListener extends Observer {
    @Subscribe
    @AllowConcurrentEvents
    void handleconversionEvent(AsynchronousConversionEvent<String> evt);
}