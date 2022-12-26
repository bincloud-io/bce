package cloud.bangover.domain;

import cloud.bangover.events.EventType;
import cloud.bangover.events.EventType.WrongEventTypeFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EventTypeTest {
  private static final String EVENT_TYPE_NAME = "EVENT_TYPE";

  @Test
  public void shouldCreateEventType() {
    // When
    EventType<Object> eventType = EventType.createFor(EVENT_TYPE_NAME);
    // Then
    Assert.assertEquals(EVENT_TYPE_NAME, eventType.extract());
    Assert.assertEquals(String.format("%s[java.lang.Object]", EVENT_TYPE_NAME),
        eventType.toString());
  }

  @Test(expected = WrongEventTypeFormatException.class)
  public void shouldCreateEventTypeForBadFormattedTypeName() {
    EventType.createFor("Bad formatted event type!!!!!!!!");
  }

  @Test
  public void shouldCheckThatEventInstanceIsAcceptableForType() {
    // When
    EventType<Long> eventType = EventType.createFor(EVENT_TYPE_NAME, Long.class);
    // Then
    Assert.assertFalse(eventType.isAccepts(new Object()));
  }
}
