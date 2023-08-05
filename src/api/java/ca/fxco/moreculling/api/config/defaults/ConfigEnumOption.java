package ca.fxco.moreculling.api.config.defaults;

import ca.fxco.moreculling.api.config.ConfigOption;
import ca.fxco.moreculling.api.config.ConfigOptionFlag;
import ca.fxco.moreculling.api.config.ConfigOptionImpact;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Enum option that can be used through the API
 * This is a pre-made basic enum option that simply stores all the values to be used by the MoreCulling Config API
 *
 * @since 0.12.0
 */
public class ConfigEnumOption<T extends Enum<?>> implements ConfigOption<T> {

    private final String translationKey;
    private final Consumer<T> setter;
    private final Supplier<T> getter;
    private final Consumer<T> changed;
    private final T defaultValue;
    private final ConfigOptionImpact impact;
    private final ConfigOptionFlag flag;
    private final Class<T> typeClass;
    private final Text[] localizedNames;

    public ConfigEnumOption(String translationKey, Consumer<T> setter, Supplier<T> getter, Consumer<T> changed,
                            T defaultValue, ConfigOptionImpact impact, ConfigOptionFlag flag, Class<T> typeClass,
                            Text[] localizedNames) {
        this.translationKey = translationKey;
        this.setter = setter;
        this.getter = getter;
        this.changed = changed;
        this.defaultValue = defaultValue;
        this.impact = impact;
        this.flag = flag;
        this.typeClass = typeClass;
        this.localizedNames = localizedNames;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public Consumer<T> getSetter() {
        return setter;
    }

    @Override
    public Supplier<T> getGetter() {
        return getter;
    }

    @Override
    public Consumer<T> getChanged() {
        return changed;
    }

    @Override
    public T getDefaultValue() {
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

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Text[] getLocalizedNames() {
        return localizedNames;
    }
}