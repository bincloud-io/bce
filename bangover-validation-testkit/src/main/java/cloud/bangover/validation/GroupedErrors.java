package cloud.bangover.validation;

import cloud.bangover.validation.ValidationState.ErrorState;
import cloud.bangover.validation.ValidationState.GroupedError;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * This class is usable for specified group error messages extracting from the specified
 * {@link ErrorState}. This class is usable only for testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@RequiredArgsConstructor
public class GroupedErrors {
  private final String groupName;
  private final ErrorState errorState;

  /**
   * Get collection of {@link ErrorMessage} from {@link ErrorState}, grouped by specified group.
   *
   * @param groupName  The group name
   * @param errorState The {@link ErrorState}
   * @return The errors messages
   */
  public static Collection<ErrorMessage> errorsOf(String groupName, ErrorState errorState) {
    return new GroupedErrors(groupName, errorState).getErrors();
  }

  /**
   * Get collection of {@link ErrorMessage}.
   *
   * @return The errors messages 
   */
  public Collection<ErrorMessage> getErrors() {
    Set<ErrorMessage> errorMessages = new HashSet<ErrorMessage>();
    for (GroupedError groupedError : errorState.getGroupedErrors()) {
      if (groupName.equals(groupedError.getGroupName())) {
        errorMessages.addAll(groupedError.getMessages());
      }
    }
    return errorMessages;
  }

}
