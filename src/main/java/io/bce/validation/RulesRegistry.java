package io.bce.validation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.bce.validation.ValidationContext.Rule;
import lombok.RequiredArgsConstructor;

public class RulesRegistry {
	private static final Map<String, Rule<Object>> REGISTERED_RULES = new ConcurrentHashMap<>();
	
	/**
	 * Register rule into registry
	 * 
	 * @param <T> The validatable value type
	 * @param alias The rule alias 
	 * @param rule The registered rule
	 * @throws RuleHasAlreadyBeenRegisteredException if rule is registered 
	 */
	@SuppressWarnings("unchecked")
	public static final <T> void registerRule(String alias, Rule<T> rule) {
		checkThatRuleIsNotRegistered(alias);
		REGISTERED_RULES.put(alias, (Rule<Object>) rule);
	}
	
	/**
	 * Get rule by alias
	 * 
	 * @param <T> The 
	 * @param alias
	 * @return
	 */
	public static final <T> Rule<T> getRule(String alias) {
		return new RegistryRuleProxy<>(alias);
	}
	
	public static final void clear() {
		REGISTERED_RULES.clear();
	}
	
	private static final void checkThatRuleIsNotRegistered(String alias) {
		if (REGISTERED_RULES.containsKey(alias)) {
			throw new RuleHasAlreadyBeenRegisteredException(alias);
		}
	}
	
	public static final class RuleHasAlreadyBeenRegisteredException extends RuntimeException {
		private static final long serialVersionUID = -1455331418709482737L;

		public RuleHasAlreadyBeenRegisteredException(String alias) {
			super(String.format("Rule with [%s] alias has already been registered", alias));
		}
	}
	
	@RequiredArgsConstructor
	private static class RegistryRuleProxy<T> implements Rule<T> {
		private final Optional<Rule<Object>> rule;

		public RegistryRuleProxy(String alias) {
			this(Optional.ofNullable(REGISTERED_RULES.get(alias)));
		}
						
		@Override
		public boolean isAcceptableFor(T value) {
			return rule.map(rule -> rule.isAcceptableFor(value)).orElse(false);
		}

		@Override
		public Collection<ErrorMessage> check(T value) {
			return rule.get().check(value);
		}

		@Override
		public Rule<T> invert() {
			return new RegistryRuleProxy<>(rule.map(Rule::invert));
		}
	}
}
