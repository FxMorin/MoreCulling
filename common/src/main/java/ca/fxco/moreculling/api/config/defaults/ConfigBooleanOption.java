package ca.fxco.moreculling.api.config.defaults;

import ca.fxco.moreculling.api.config.ConfigOption;
import ca.fxco.moreculling.api.config.ConfigOptionFlag;
import ca.fxco.moreculling.api.config.ConfigOptionImpact;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Boolean option that can be used through the API.
 * This is a pre-made basic boolean option that simply stores all the values to be used by the MoreCulling Config API
 *
 * @since 0.12.0
 */
public class ConfigBooleanOption implements ConfigOption<Boolean> {

    private final String translationKey;
    private final Consumer<Boolean> setter;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> changed;
    private final Boolean defaultValue;
    private final ConfigOptionImpact impact;
    private final ConfigOptionFlag flag;

    public ConfigBooleanOption(String translationKey, Consumer<Boolean> setter, Supplier<Boolean> getter,
                               Consumer<Boolean> changed, Boolean defaultValue, ConfigOptionImpact impact,
                               ConfigOptionFlag flag) {
        this.translationKey = translationKey;
        this.setter = setter;
        this.getter = getter;
        this.changed = changed;
        this.defaultValue = defaultValue;
        this.impact = impact;
        this.flag = flag;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public Consumer<Boolean> getSetter() {
        return setter;
    }

    @Override
    public Supplier<Boolean> getGetter() {
        return getter;
    }

    @Override
    public Consumer<Boolean> getChanged() {
        return changed;
    }

    @Override
    public Boolean getDefaultValue() {
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
}