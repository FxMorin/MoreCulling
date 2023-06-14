package ca.fxco.moreculling.api.config.defaults;

import ca.fxco.moreculling.api.config.ConfigOption;
import ca.fxco.moreculling.api.config.ConfigOptionFlag;
import ca.fxco.moreculling.api.config.ConfigOptionImpact;
import ca.fxco.moreculling.api.config.ConfigSliderOption;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Float option that can be used through the API
 * This is a pre-made basic float option that simply stores all the values to be used by the MoreCulling Config API
 * @since 0.12.0
 */
public class ConfigFloatOption implements ConfigOption<Float>, ConfigSliderOption<Float> {

    private final String translationKey;
    private final Consumer<Float> setter;
    private final Supplier<Float> getter;
    private final Consumer<Float> changed;
    private final Float defaultValue;
    private final ConfigOptionImpact impact;
    private final ConfigOptionFlag flag;
    private final float min;
    private final float max;
    private final float interval;
    private final String stringFormat;
    public ConfigFloatOption(String translationKey, Consumer<Float> setter, Supplier<Float> getter,
                             Consumer<Float> changed, Float defaultValue, ConfigOptionImpact impact,
                             ConfigOptionFlag flag, float min, float max, float interval, String stringFormat) {
        this.translationKey = translationKey;
        this.setter = setter;
        this.getter = getter;
        this.changed = changed;
        this.defaultValue = defaultValue;
        this.impact = impact;
        this.flag = flag;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.stringFormat = stringFormat;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Consumer<Float> getSetter() {
        return setter;
    }

    public Supplier<Float> getGetter() {
        return getter;
    }

    public Consumer<Float> getChanged() {
        return changed;
    }

    public Float getDefaultValue() {
        return defaultValue;
    }

    public ConfigOptionImpact getImpact() {
        return impact;
    }

    public ConfigOptionFlag getFlag() {
        return flag;
    }

    public Float getMin() {
        return min;
    }

    public Float getMax() {
        return max;
    }

    public Float getInterval() {
        return interval;
    }

    public String getStringFormat() {
        return stringFormat;
    }
}