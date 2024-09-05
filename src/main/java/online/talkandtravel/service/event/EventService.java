package online.talkandtravel.service.event;

public interface EventService<T> {

  void publishEvent(T payload, Object... args);

}
