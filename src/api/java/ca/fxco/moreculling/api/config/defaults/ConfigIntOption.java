package ca.fxco.moreculling.api.config.defaults;

import ca.fxco.moreculling.api.config.ConfigOption;
import ca.fxco.moreculling.api.config.ConfigOptionFlag;
import ca.fxco.moreculling.api.config.ConfigOptionImpact;
import ca.fxco.moreculling.api.config.ConfigSliderOption;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Int option that can be used through the API
 * This is a pre-made basic int option that simply stores all the values to be used by the MoreCulling Config API
 * @since 0.12.0
 */
public class ConfigIntOption implements ConfigOption<Integer>, ConfigSliderOption<Integer> {

    private final String translationKey;
    private final Consumer<Integer> setter;
    private final Supplier<Integer> getter;
    private final Consumer<Integer> changed;
    private final Integer defaultValue;
    private final ConfigOptionImpact impact;
    private final ConfigOptionFlag flag;
    private final int min;
    private final int max;
    private final int interval;
    private final String stringFormat;

    public ConfigIntOption(String translationKey, Consumer<Integer> setter, Supplier<Integer> getter,
                           Consumer<Integer> changed, Integer defaultValue, ConfigOptionImpact impact,
                           ConfigOptionFlag flag, int min, int max, int interval, String stringFormat) {
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

    @Override
    public Consumer<Integer> getSetter() {
        return setter;
    }

    @Override
    public Supplier<Integer> getGetter() {
        return getter;
    }

    @Override
    public Consumer<Integer> getChanged() {
        return changed;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public Integer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public ConfigOptionImpact getImpact() {
        return impact;
    }

    @Override
    public ConfigOptionFlag getFlag() {
        return flag;
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    @Override
    public Integer getInterval() {
        return interval;
    }

    @Override
    public String getStringFormat() {
        return stringFormat;
    }
}