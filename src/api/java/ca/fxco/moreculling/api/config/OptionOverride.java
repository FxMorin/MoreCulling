package ca.fxco.moreculling.api.config;

import java.util.function.BooleanSupplier;

/**
 * This controls how the options should be overridden.
 *
 * @since 0.24.0
 * @author FX
 */
public record OptionOverride(String reason, BooleanSupplier canChange) {}
