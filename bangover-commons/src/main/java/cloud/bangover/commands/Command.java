package cloud.bangover.commands;

/**
 * This interface describes the commands. Pattern command is described in the book "The Gang of
 * Four" and represents executable operation as an OOP object.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The response data type.
 */
public interface Command<T> {
  /**
   * Execute command.
   *
   * @return The command object.
   */
  public T execute();
}
