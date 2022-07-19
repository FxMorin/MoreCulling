package ca.fxco.moreculling.config.sodium;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.binding.GenericBinding;
import me.jellysquid.mods.sodium.client.gui.options.binding.OptionBinding;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MoreCullingOptionImpl<S, T> implements Option<T> {

    /*
     * All this just because Sodium has to set there damn enabled to be final.
     * I then also put a couple more things in there ;)
     */

    protected final OptionStorage<S> storage;

    protected final OptionBinding<S, T> binding;
    protected final Control<T> control;

    protected Consumer<Boolean> onEnabledChanged;

    protected final EnumSet<OptionFlag> flags;

    protected final Text name;
    protected final Text tooltip;

    protected final OptionImpact impact;

    protected T value;
    protected T modifiedValue;

    protected boolean enabled;

    protected MoreCullingOptionImpl(OptionStorage<S> storage, Text name, Text tooltip, OptionBinding<S, T> binding,
                                  Function<MoreCullingOptionImpl<S, T>, Control<T>> control, EnumSet<OptionFlag> flags,
                                  OptionImpact impact, Consumer<Boolean> valueModified, boolean enabled) {
        this.storage = storage;
        this.name = name;
        this.tooltip = tooltip;
        this.binding = binding;
        this.impact = impact;
        this.flags = flags;
        this.control = control.apply(this);
        this.onEnabledChanged = valueModified;
        this.enabled = enabled;

        this.reset();
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public Text getTooltip() {
        return this.tooltip;
    }

    @Override
    public OptionImpact getImpact() {
        return this.impact;
    }

    @Override
    public Control<T> getControl() {
        return this.control;
    }

    @Override
    public T getValue() {
        return this.modifiedValue;
    }

    @Override
    public void setValue(T value) {
        this.modifiedValue = value;
        if (this.onEnabledChanged != null)
            this.onEnabledChanged.accept(this.enabled && this.modifiedValue instanceof Boolean bool ? bool : true);
    }

    @Override
    public void reset() {
        this.value = this.binding.getValue(this.storage.getData());
        this.modifiedValue = this.value;
        if (this.onEnabledChanged != null)
            this.onEnabledChanged.accept(this.enabled && this.modifiedValue instanceof Boolean bool ? bool : true);
    }

    @Override
    public OptionStorage<?> getStorage() {
        return this.storage;
    }

    @Override
    public boolean isAvailable() {
        return this.enabled;
    }

    public void setAvailable(boolean available) {
        this.enabled = available;
        if (this.onEnabledChanged != null)
            this.onEnabledChanged.accept(available);
    }

    public void setEnabledChanged(Consumer<Boolean> valueModified) {
        this.onEnabledChanged = valueModified;
    }

    @Override
    public boolean hasChanged() {
        return !this.value.equals(this.modifiedValue);
    }

    @Override
    public void applyChanges() {
        this.binding.setValue(this.storage.getData(), this.modifiedValue);
        this.value = this.modifiedValue;
    }

    @Override
    public Collection<OptionFlag> getFlags() {
        return this.flags;
    }

    public static <S, T> MoreCullingOptionImpl.Builder<S, T> createBuilder(Class<T> type, OptionStorage<S> storage) {
        return new Builder<>(storage);
    }

    public static class Builder<S, T> {
        private final OptionStorage<S> storage;
        private Text name;
        private Text tooltip;
        private OptionBinding<S, T> binding;
        private Function<MoreCullingOptionImpl<S, T>, Control<T>> control;
        private OptionImpact impact;
        private final EnumSet<OptionFlag> flags = EnumSet.noneOf(OptionFlag.class);
        private Consumer<Boolean> enabledChanged;
        private boolean enabled = true;

        private Builder(OptionStorage<S> storage) {
            this.storage = storage;
        }

        public Builder<S, T> setName(@Nullable Text name) {
            this.name = name;
            return this;
        }

        public Builder<S, T> setTooltip(@Nullable Text tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder<S, T> setBinding(BiConsumer<S, T> setter, Function<S, T> getter) {
            Validate.notNull(setter, "Setter must not be null");
            Validate.notNull(getter, "Getter must not be null");
            this.binding = new GenericBinding<>(setter, getter);
            return this;
        }

        public Builder<S, T> setBinding(OptionBinding<S, T> binding) {
            Validate.notNull(binding, "Argument must not be null");
            this.binding = binding;
            return this;
        }

        public Builder<S, T> setControl(Function<MoreCullingOptionImpl<S, T>, Control<T>> control) {
            Validate.notNull(control, "Argument must not be null");
            this.control = control;
            return this;
        }

        public Builder<S, T> setImpact(OptionImpact impact) {
            this.impact = impact;
            return this;
        }

        public Builder<S, T> onEnabledChanged(Consumer<Boolean> consumer) {
            Validate.notNull(consumer, "Runnable must not be null");
            this.enabledChanged = consumer;
            return this;
        }

        public Builder<S, T> setEnabled(boolean value) {
            this.enabled = value;
            return this;
        }

        public Builder<S, T> setFlags(OptionFlag... flags) {
            Collections.addAll(this.flags, flags);
            return this;
        }

        public MoreCullingOptionImpl<S, T> build() {
            Validate.notNull(this.name, "Name must be specified");
            Validate.notNull(this.tooltip, "Tooltip must be specified");
            Validate.notNull(this.binding, "Option binding must be specified");
            Validate.notNull(this.control, "Control must be specified");
            return new MoreCullingOptionImpl<>(this.storage, this.name, this.tooltip, this.binding, this.control, this.flags, this.impact, this.enabledChanged, this.enabled);
        }
    }
}
