package js.tiny.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import js.lang.BugError;
import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;

public class EventsManager implements IEventsManager {
	private static final Log log = LogFactory.getLog(EventsManager.class);

	private final Map<Integer, BlockingQueue<Event>> queues = new HashMap<>();

	@Override
	public synchronized BlockingQueue<Event> acquireQueue(Integer eventsServletID) {
		if (queues.get(eventsServletID) != null) {
			throw new BugError("Queue for events servlet |%d| already created.", eventsServletID);
		}
		BlockingQueue<Event> queue = new LinkedBlockingDeque<>();
		queues.put(eventsServletID, queue);
		log.debug("Acquired events queue |%d|.", eventsServletID);
		return queue;
	}

	@Override
	public synchronized void releaseQueue(Integer eventsServletID) {
		if (queues.get(eventsServletID) == null) {
			throw new BugError("Missing queue for events servlet |%d|.", eventsServletID);
		}
		queues.remove(eventsServletID);
		log.debug("Released events queue |%d|.", eventsServletID);
	}

	@Override
	public synchronized void pushEvent(Event event) {
		for (BlockingQueue<Event> queue : queues.values()) {
			queue.offer(event);
		}
	}
}
