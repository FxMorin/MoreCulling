package ca.fxco.moreculling.config.sodium;

import ca.fxco.moreculling.api.config.ConfigAdditions;
import ca.fxco.moreculling.api.config.OptionOverride;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionFlag;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.options.binding.GenericBinding;
import net.caffeinemc.mods.sodium.client.gui.options.binding.OptionBinding;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MoreCullingSodiumOptionImpl<S, T> implements Option<T> {

    /*
     * Custom implementation of Sodium's OptionImpl which allows me to do stuff such as:
     * - Dynamic state changing (enabled, values, events, etc...)
     * - Custom text formatter
     * - Easy Mod compatibility checks
     */

    protected final OptionStorage<S> storage;

    protected final OptionBinding<S, T> binding;
    protected final Control<T> control;

    protected BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> onChanged;

    protected final EnumSet<OptionFlag> flags;

    protected final Component name;
    protected final Component tooltip;

    protected final OptionImpact impact;

    protected T value;
    protected T modifiedValue;

    protected boolean enabled;
    private final boolean locked; // Prevents anything from changing

    protected MoreCullingSodiumOptionImpl(
            OptionStorage<S> storage, Component name, Component tooltip, OptionBinding<S, T> binding,
            Function<MoreCullingSodiumOptionImpl<S, T>, Control<T>> control, EnumSet<OptionFlag> flags,
            OptionImpact impact, BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> onChanged, boolean enabled
    ) {
        this(storage, name, tooltip, binding, control, flags, impact, onChanged, enabled, false);
    }

    protected MoreCullingSodiumOptionImpl(
            OptionStorage<S> storage, Component name, Component tooltip, OptionBinding<S, T> binding,
            Function<MoreCullingSodiumOptionImpl<S, T>, Control<T>> control, EnumSet<OptionFlag> flags,
            OptionImpact impact, BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> onChanged,
            boolean enabled, boolean locked
    ) {
        this.storage = storage;
        this.name = name;
        this.tooltip = tooltip;
        this.binding = binding;
        this.impact = impact;
        this.flags = flags;
        this.control = control.apply(this);
        this.onChanged = onChanged;
        this.enabled = enabled;
        this.locked = locked;

        this.reset();
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getTooltip() {
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
        if (this.locked) {
            return;
        }
        this.modifiedValue = value;
        if (this.onChanged != null) {
            this.onChanged.accept(this, this.getValue());
        }
    }

    @Override
    public void reset() {
        this.value = this.binding.getValue(this.storage.getData());
        this.modifiedValue = this.value;
        if (this.onChanged != null) {
            this.onChanged.accept(this, this.getValue());
        }
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
        if (this.locked) {
            return;
        }
        this.enabled = available;
        if (this.onChanged != null) {
            this.onChanged.accept(this, this.getValue());
        }
    }

    public void setOnChanged(BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> onChanged) {
        if (this.locked) {
            return;
        }
        this.onChanged = onChanged;
    }

    @Override
    public boolean hasChanged() {
        return !this.value.equals(this.modifiedValue);
    }

    @Override
    public void applyChanges() {
        if (this.locked) {
            return;
        }
        if (this.enabled) {
            this.binding.setValue(this.storage.getData(), this.modifiedValue);
            this.value = this.modifiedValue;
        }
    }

    @Override
    public Collection<OptionFlag> getFlags() {
        return this.flags;
    }

    public static <S, T> Builder<S, T> createBuilder(Class<T> type, OptionStorage<S> storage) {
        return new Builder<>(storage);
    }

    /*
     Builder is fully in control of mod compatibility checks
     */

    public static class Builder<S, T> {
        private final OptionStorage<S> storage;
        private String nameTranslationKey;
        private Component tooltip;
        private OptionBinding<S, T> binding;
        private Function<MoreCullingSodiumOptionImpl<S, T>, Control<T>> control;
        private OptionImpact impact;
        private final EnumSet<OptionFlag> flags = EnumSet.noneOf(OptionFlag.class);
        @Nullable
        private BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> onChanged;
        private boolean enabled = true;
        private boolean locked = false;

        private Builder(OptionStorage<S> storage) {
            this.storage = storage;
        }

        public Builder<S, T> setNameTranslation(String translationKey) {
            this.nameTranslationKey = translationKey;
            return this;
        }

        public Builder<S, T> setTooltip(@Nullable Component tooltip) {
            if (!this.locked) {
                this.tooltip = tooltip;
            }
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

        public Builder<S, T> setControl(Function<MoreCullingSodiumOptionImpl<S, T>, Control<T>> control) {
            Validate.notNull(control, "Argument must not be null");
            this.control = control;
            return this;
        }

        public Builder<S, T> setImpact(OptionImpact impact) {
            this.impact = impact;
            return this;
        }

        public Builder<S, T> onChanged(BiConsumer<MoreCullingSodiumOptionImpl<S, T>, T> biconsumer) {
            Validate.notNull(biconsumer, "BiConsumer must not be null");
            if (!this.locked) {
                this.onChanged = biconsumer;
            }
            return this;
        }

        public Builder<S, T> setEnabled(boolean value) {
            if (!this.locked) {
                this.enabled = value;
            }
            return this;
        }

        public Builder<S, T> setFlags(OptionFlag... flags) {
            Collections.addAll(this.flags, flags);
            return this;
        }

        public Builder<S, T> setModIncompatibility(boolean isLoaded, String modId) {
            if (isLoaded) {
                this.locked = true;
                this.enabled = false;
                this.tooltip = Component.translatable("moreculling.config.optionDisabled", modId);
                this.onChanged = null;
            }
            return this;
        }

        public Builder<S, T> setModLimited(boolean isLoaded, Component limitedMessage) {
            if (isLoaded) {
                this.tooltip = this.tooltip != null ? this.tooltip.copy().append("\n").append(limitedMessage) : limitedMessage;
            }
            return this;
        }

        public MoreCullingSodiumOptionImpl<S, T> build() {
            Validate.notNull(this.nameTranslationKey, "Name must be specified");
            Validate.notNull(this.tooltip, "Tooltip must be specified");
            Validate.notNull(this.binding, "Option binding must be specified");
            Validate.notNull(this.control, "Control must be specified");
            OptionOverride optionOverride = ConfigAdditions.getDisabledOptions().get(this.nameTranslationKey);
            if (optionOverride != null && !optionOverride.canChange().getAsBoolean()) {
                this.locked = true;
                this.tooltip = Component.literal(optionOverride.reason());
            }
            return new MoreCullingSodiumOptionImpl<>(
                    this.storage, Component.translatable(this.nameTranslationKey), this.tooltip, this.binding, this.control,
                    this.flags, this.impact, this.onChanged, this.enabled, this.locked
            );
        }
    }
}
