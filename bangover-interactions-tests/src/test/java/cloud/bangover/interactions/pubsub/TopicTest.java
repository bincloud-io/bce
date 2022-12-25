package cloud.bangover.interactions.pubsub;

import cloud.bangover.interactions.pubsub.Topic.WrongTopicNameFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TopicTest {
  private static final String CORRECT_TOPIC_NAME_STRING = "TOPIC-NAME.T_12345";
  private static final String WRONG_TOPIC_NAME_STRING = "TOPIC-NAME.T_1 2 3 4 5";
  
  @Test
  public void shouldTopicNameBeCreatedFromWellFormattedString() {
    // When
    Topic topicName = Topic.ofName(CORRECT_TOPIC_NAME_STRING);

    // Then
    Assert.assertEquals(CORRECT_TOPIC_NAME_STRING, topicName.toString());;
  }
  
  @Test(expected = WrongTopicNameFormatException.class)
  public void shouldTopicNameNotToBeCreatedFromBFormattedString() {
    Topic.ofName(WRONG_TOPIC_NAME_STRING);
  }
}
