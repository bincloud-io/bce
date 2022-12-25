package cloud.bangover.interactions.pubsub;

import cloud.bangover.actors.ActorAddress;
import cloud.bangover.interactions.pubsub.MessagingCoordinatorActor.Subscribtions;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This class describes the "unsubscribe" command.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Unsubscribe {
  private final Topic topic;
  private final ActorAddress actorAddress;

  void unsubscribe(Subscribtions subscribtions) {
    subscribtions.unsubscribe(topic, actorAddress);
  }
}