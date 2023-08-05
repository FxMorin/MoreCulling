package ca.fxco.moreculling.api.config;

/**
 * Adds the ModLimit methods to a ConfigOption
 *
 * @since 0.12.0
 */
public interface ConfigModLimit {

    /**
     * The modId that this option is limited with
     *
     * @since 0.12.0
     */
    String getLimitedModId();

    /**
     * Tre translation key to use when limited
     *
     * @since 0.12.0
     */
    default String getTranslationKey() {
        return "";
    }
}