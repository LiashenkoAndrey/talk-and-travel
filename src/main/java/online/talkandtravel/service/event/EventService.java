package online.talkandtravel.service.event;

public interface EventService<T> {

  /**
   *
   * @param payload
   * @param args
   */
  void publishEvent(T payload, Object... args);

}
