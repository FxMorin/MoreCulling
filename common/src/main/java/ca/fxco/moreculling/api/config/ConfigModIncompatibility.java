package ca.fxco.moreculling.api.config;

/**
 * Adds the ModIncompatibility methods to a ConfigOption
 *
 * @since 0.12.0
 */
public interface ConfigModIncompatibility {

    /**
     * The modId that this option is incompatible with
     *
     * @since 0.12.0
     */
    String getIncompatibleModId();

    /**
     * The message to display when running into this incompatibility
     *
     * @since 0.12.0
     */
    default String getMessage() {
        return "";
    }
}