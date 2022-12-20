package cloud.bangover.validation;

import cloud.bangover.validation.ValidationState.ErrorState;
import cloud.bangover.validation.ValidationState.GroupedError;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GroupedErrors {
  private String groupName;
  private ErrorState errorState;

  public GroupedErrors(String groupName, ErrorState errorState) {
    super();
    this.groupName = groupName;
    this.errorState = errorState;
  }

  public Collection<ErrorMessage> getErrors() {
    Set<ErrorMessage> errorMessages = new HashSet<ErrorMessage>();
    for (GroupedError groupedError : errorState.getGroupedErrors()) {
      if (groupName.equals(groupedError.getGroupName())) {
        errorMessages.addAll(groupedError.getMessages());
      }
    }
    return errorMessages;
  }

  public static Collection<ErrorMessage> errorsOf(String groupName, ErrorState errorState) {
    return new GroupedErrors(groupName, errorState).getErrors();
  }
}
