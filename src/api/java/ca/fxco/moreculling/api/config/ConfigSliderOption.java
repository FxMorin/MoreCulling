package ca.fxco.moreculling.api.config;

/**
 * Add Slider options to ConfigOptions
 * @since 0.12.0
 */
public interface ConfigSliderOption<T> {

    /**
     * The minimum value allowed in the slider
     * @since 0.12.0
     */
    T getMin();

    /**
     * The maximum value allowed in the slider
     * @since 0.12.0
     */
    T getMax();

    /**
     * The interval which the slider should skip by
     * @since 0.12.0
     */
    T getInterval();

    /**
     * How the values in the slider should be formatted
     * @since 0.12.0
     */
    String getStringFormat();
}