package cloud.bangover.domain.buses;

import cloud.bangover.BoundedContextId;
import cloud.bangover.domain.EventBus;
import cloud.bangover.domain.EventBus.EventSubscribtion;
import cloud.bangover.domain.EventPublisher;
import cloud.bangover.domain.EventType;
import cloud.bangover.domain.MockEventListener;
import cloud.bangover.domain.buses.PubSubEventBus.UnacceptableEventException;
import cloud.bangover.interactions.pubsub.LocalPubSub;
import cloud.bangover.interactions.pubsub.PubSub;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PubSubEventBusTest {
  private static final BoundedContextId CONTEXT = BoundedContextId.createFor("CONTEXT");
  private static final EventType<Long> EVENT_TYPE = EventType.createFor("EVENT", Long.class);
  private static final EventType<String> WRONG_INITIALIZED_EVENT_TYPE = EventType.createFor("EVENT", String.class);
  
  @Test
  public void shouldPublishEventsToEventBus() {
    // Given
    PubSub<Object> channel = new LocalPubSub<Object>();
    EventBus eventBus = PubSubEventBus.factory(channel).createEventBus();
    MockEventListener<Long> eventListener = new MockEventListener<Long>();
    EventSubscribtion eventSubscribtion = eventBus.subscribeOn(CONTEXT, EVENT_TYPE, eventListener);
    // When
    eventBus.getPublisher(CONTEXT, EVENT_TYPE).publish(1L);
    eventSubscribtion.unsubscribe();

    // Then
    Assert.assertTrue(eventListener.getHistory().hasEntry(0, 1L));
  }
  
  @Test(expected = UnacceptableEventException.class)
  public void shouldFailOnWrongEventTypePublishing() {
    // Given    
    PubSub<Object> channel = new LocalPubSub<Object>();
    EventBus eventBus = PubSubEventBus.factory(channel).createEventBus();

    // When
    @SuppressWarnings("unchecked")
    EventPublisher<Object> eventPublisher = (EventPublisher<Object>) ((Object) eventBus.getPublisher(CONTEXT, EVENT_TYPE));
    eventPublisher.publish("HELLO");
  }
  
  @Test
  public void shouldNotReactOnUnregisteredType() {
    // Given
    PubSub<Object> channel = new LocalPubSub<Object>();
    EventBus eventBus = PubSubEventBus.factory(channel).createEventBus();
    MockEventListener<String> eventListener = new MockEventListener<String>();
    EventSubscribtion eventSubscribtion = eventBus.subscribeOn(CONTEXT, WRONG_INITIALIZED_EVENT_TYPE, eventListener);
    // When
    eventBus.getPublisher(CONTEXT, EVENT_TYPE).publish(1L);
    eventSubscribtion.unsubscribe();
    // Then
    Assert.assertFalse(eventListener.getHistory().isNotEmpty());
  }
}
