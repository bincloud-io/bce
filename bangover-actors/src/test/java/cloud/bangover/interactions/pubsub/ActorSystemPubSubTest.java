package cloud.bangover.interactions.pubsub;

import cloud.bangover.actors.ActorName;
import cloud.bangover.actors.ActorSystem;
import cloud.bangover.actors.Actors;
import cloud.bangover.actors.Actors.SystemConfiguration;
import cloud.bangover.actors.Actors.SystemConfigurer;
import cloud.bangover.actors.CorrelationKey;
import cloud.bangover.actors.EventLoop.Dispatcher;
import cloud.bangover.actors.FixedMessagesWaitingDispatcher;
import cloud.bangover.generators.StubGenerator;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ActorSystemPubSubTest {
  private static final ActorName COORDINATOR_NAME = ActorName.wrap("PUB_SUB_COORDINATOR");
  private static final Topic TOPIC_1 = Topic.ofName("TOPIC.1");
  private static final Topic TOPIC_2 = Topic.ofName("TOPIC.2");
  private static final String MESSAGE_1 = "TEST_MESSAGE";
  private static final Long MESSAGE_2 = -12345L;
  
  @Test
  public void shouldPublishEventsToTopicsAndConsumeBySubscribers() {
    // Given
    MockSubscriber<String> firstSubscriber = new MockSubscriber<String>();
    MockSubscriber<String> secondSubscriber = new MockSubscriber<String>();
    MockSubscriber<Long> thirdSubscriber = new MockSubscriber<Long>();
    FixedMessagesWaitingDispatcher dispatcher = FixedMessagesWaitingDispatcher.singleThreadDispatcher(11);
    ActorSystem actorSystem = createActorSystem(dispatcher);
    actorSystem.start();
    PubSub<Object> pubSubChannel = ActorSystemPubSub.factory(actorSystem).createPubSub(COORDINATOR_NAME);
    // When
    Subscribtion firstSubscribtion = pubSubChannel.subscribeOn(TOPIC_1, String.class, firstSubscriber);
    Subscribtion secondSubscribtion = pubSubChannel.subscribeOn(TOPIC_1, String.class, secondSubscriber);
    Subscribtion thirdSubscribtion = pubSubChannel.subscribeOn(TOPIC_2, Long.class, thirdSubscriber);
    Publisher<Object> publisherTopic1 = pubSubChannel.getPublisher(TOPIC_1);
    Publisher<Object> publisherTopic2 = pubSubChannel.getPublisher(TOPIC_2);
    publisherTopic1.publish(MESSAGE_1);
    publisherTopic2.publish(MESSAGE_2);
    firstSubscribtion.unsubscribe();
    secondSubscribtion.unsubscribe();
    thirdSubscribtion.unsubscribe();
    pubSubChannel.close();
    dispatcher.getWaiter().await();
    actorSystem.shutdown();
    // Then
    Assert.assertTrue(firstSubscriber.hasReceovedMessage(0, MESSAGE_1));
    Assert.assertTrue(secondSubscriber.hasReceovedMessage(0, MESSAGE_1));
    Assert.assertTrue(thirdSubscriber.hasReceovedMessage(0, MESSAGE_2));
  }

  private ActorSystem createActorSystem(Dispatcher dispatcher) {
    return Actors.create(new Actors.SystemInitializer() {
      @Override
      public SystemConfiguration initializeSystem(SystemConfigurer configurer) {
        return configurer
            .withDispatcher(dispatcher)
            .withCorrelationKeyGenerator(new StubGenerator<CorrelationKey>(CorrelationKey.wrap(UUID.randomUUID().toString())))
            .configure();
      }
    });
  }
}
