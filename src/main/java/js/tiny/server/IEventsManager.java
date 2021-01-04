package js.tiny.server;

import java.util.concurrent.BlockingQueue;

import js.lang.Event;

public interface IEventsManager {
    BlockingQueue<Event> acquireQueue(Integer eventsServletID);

    void releaseQueue(Integer eventsServletID);

    void pushEvent(Event event);
}
