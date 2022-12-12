package cloud.bangover.logging.audit;

import cloud.bangover.logging.ApplicationLogger;

/**
 * This class declares the cross-framework contract for logging internal audit events. It doesn't
 * should be used instead {@link ApplicationLogger}. Use this only for fixation most important
 * facts. For example about payment transaction or massive incidents.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public interface ServiceAuditLogger {
  /**
   * Log service audit event.
   *
   * @param auditEvent The audit event
   */
  public void log(ServiceAuditEvent auditEvent);
}
