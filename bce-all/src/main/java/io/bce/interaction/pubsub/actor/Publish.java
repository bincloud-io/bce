package io.bce.interaction.pubsub.actor;

import cloud.bangover.interactions.pubsub.Topic;
import io.bce.actor.Message;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This class describes the "publish" command.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Publish {
  private final Topic topic;
  private final Object messageBody;

  public void publish(Message<Object> message, Publish.PublishCommand publishHandler) {
    publishHandler.publish(topic, message.map(v -> messageBody));
  }

  interface PublishCommand {
    public void publish(Topic topic, Message<Object> message);
  }
}