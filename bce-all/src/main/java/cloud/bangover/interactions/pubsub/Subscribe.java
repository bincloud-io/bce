package cloud.bangover.interactions.pubsub;

import cloud.bangover.actor.ActorAddress;
import cloud.bangover.interactions.pubsub.MessagingCoordinatorActor.Subscribtions;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This class describes the "subscribe" command.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Subscribe {
  private final Topic eventType;
  private final ActorAddress actorAddress;

  void subscribe(Subscribtions subscribtions) {
    subscribtions.subscribe(eventType, actorAddress);
  }
}