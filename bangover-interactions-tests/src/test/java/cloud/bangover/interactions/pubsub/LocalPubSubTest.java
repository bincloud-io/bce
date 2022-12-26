package cloud.bangover.interactions.pubsub;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocalPubSubTest {
  private static final Topic TOPIC_1 = Topic.ofName("TOPIC.1");
  private static final Topic TOPIC_2 = Topic.ofName("TOPIC.2");
  private static final String MESSAGE_1 = "TEST_MESSAGE";
  private static final Long MESSAGE_2 = -12345L;

  @Test
  public void shouldTranferMessagesByPubSubChannel() {
    // Given
    PubSub<Object> pubSubChannel = new LocalPubSub<Object>();
    MockSubscriber<String> firstSubscriber = new MockSubscriber<String>();
    MockSubscriber<String> secondSubscriber = new MockSubscriber<String>();
    MockSubscriber<Long> thirdSubscriber = new MockSubscriber<Long>();
    Subscribtion firstSubscribtion =
        pubSubChannel.subscribeOn(TOPIC_1, String.class, firstSubscriber);
    Subscribtion secondSubscribtion =
        pubSubChannel.subscribeOn(TOPIC_1, String.class, secondSubscriber);
    Subscribtion thirdSubscribtion =
        pubSubChannel.subscribeOn(TOPIC_2, Long.class, thirdSubscriber);
    Publisher<Object> publisherTopic1 = pubSubChannel.getPublisher(TOPIC_1);
    Publisher<Object> publisherTopic2 = pubSubChannel.getPublisher(TOPIC_2);
    // When
    publisherTopic1.publish(MESSAGE_1);
    publisherTopic2.publish(MESSAGE_2);
    firstSubscribtion.unsubscribe();
    secondSubscribtion.unsubscribe();
    thirdSubscribtion.unsubscribe();
    pubSubChannel.close();
    // Then
    Assert.assertTrue(firstSubscriber.getHistory().hasEntry(0, MESSAGE_1));
    Assert.assertTrue(secondSubscriber.getHistory().hasEntry(0, MESSAGE_1));
    Assert.assertTrue(thirdSubscriber.getHistory().hasEntry(0, MESSAGE_2));
  }
}
