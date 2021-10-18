package io.bce.domain;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class represents the bounded context identifier. It 
 * should be used everywhere, when the value has the bounded 
 * context identifier semantic and not a random string value.
 * It will prevent the situation when the bounded context 
 * identifier will be confused with another string value having
 * another semantic.
 * 
 * @author Dmitry Mikhaylenko
 *
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoundedContextId {
	private static final Pattern CONTEXT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9\\-\\_]+"); 
	public static final BoundedContextId PLATFORM_CONTEXT = new BoundedContextId("PLATFORM");
	
	@NonNull
	private final String contextId;
	
	@Override
	public String toString() {
		return contextId;
	}
	
	public static final BoundedContextId createFor(String contextName) {
		checkThatBoundedContextIdIsWellFormatted(contextName);
		BoundedContextId contextId = new BoundedContextId(contextName);
		checkThatBoundedContextIdIsNotReservedValue(contextId);
		return contextId;
	}
	
	private static final void checkThatBoundedContextIdIsWellFormatted(String contextName) {
		if (!CONTEXT_NAME_PATTERN.matcher(contextName).matches()) {
			throw new WrongBoundedContextIdFormatException(contextName);
		}
	}
	
	private static final void checkThatBoundedContextIdIsNotReservedValue(BoundedContextId contextId) {
		if (contextId.equals(PLATFORM_CONTEXT)) {
			throw new WrongBoundedContextIdFormatException(PLATFORM_CONTEXT.toString());
		}
	}
	
	public static final class WrongBoundedContextIdFormatException extends RuntimeException {
		private static final long serialVersionUID = 8870188141820368491L;
		
		public WrongBoundedContextIdFormatException(String contextIdName) {
			super(String.format("Context name \"%s\" has wrong format or is reserved", contextIdName));
		}
	}
}
