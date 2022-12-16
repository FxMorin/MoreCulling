package ca.fxco.moreculling.api.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ConfigOption is a small option type that allows you to extend the MoreCulling config. It is not recommended unless
 * your mod is a direct extension to MoreCulling and the setting fits in with the MoreCulling options
 * @since 0.12.0
 */
public interface ConfigOption<T> {

    /**
     * The translation key location for the name of the config option.
     * The tooltip translation key is just this key with `.tooltip` at the end
     * @since 0.12.0
     */
    String getTranslationKey();

    /**
     * How much of a performance impact does this option have on the game, in terms of how much faster
     * This option is currently only used in the sodium menu
     * @since 0.12.0
     */
    ConfigOptionImpact getImpact();

    /**
     * Set any specific flag which determines if any specific action needs to happen once the setting changes
     * Currently only the `REQUIRES_GAME_RESTART` option works in ModMenu, all of them work in sodium
     * @since 0.12.0
     */
    ConfigOptionFlag getFlag();

    /**
     * A consumer which sets the setting value
     * @since 0.12.0
     */
    Consumer<T> getSetter();

    /**
     * A supplier which gets the setting value
     * @since 0.12.0
     */
    Supplier<T> getGetter();

    /**
     * A consumer which gets called whenever the value changes, along with the new value
     * @since 0.12.0
     */
    Consumer<T> getChanged();

    /**
     * Returns if the value should be enabled by default
     * This is currently only used in the Sodium menu
     * @since 0.12.0
     */
    default boolean setEnabled() {
        return true;
    }

    /**
     * Returns the default value that should be used for this config option
     * This is currently only used in the ModMenu menu
     * Sodium currently determines by calling getGetter() when initialized
     * Warning, this config option may get removed in the future. @Deprecated will be added if that's the case
     * @since 0.12.0
     */
    default T getDefaultValue() {
        return null;
    }
}