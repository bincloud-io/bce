package cloud.bangover.interactions.interactor;

import cloud.bangover.Urn.WrongUrnAddressFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TargetAddressTest {
  private static final String CORRECT_URN_ADDRESS_STRING = "urn:command:CREATE_SOMETHING";
  private static final String WRONG_URN_ADDRESS_STRING = "wrong-urn:";
  
  @Test
  public void shouldTargetAddressBeCreated() {
    // When
    TargetAddress targetAddress = TargetAddress.ofUrn(CORRECT_URN_ADDRESS_STRING);
    // Then
    Assert.assertEquals(CORRECT_URN_ADDRESS_STRING, targetAddress.toString());
  }
  
  @Test(expected = WrongUrnAddressFormatException.class)
  public void shouldTargetAddressCreationBeFailed() {
    TargetAddress.ofUrn(WRONG_URN_ADDRESS_STRING);
  }
}
