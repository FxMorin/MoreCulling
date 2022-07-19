package ca.fxco.moreculling.config.cloth;

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DynamicEnumBuilder<T extends Enum<?>> extends FieldBuilder<T, DynamicEnumEntry<T>> {
    @Nullable
    private Consumer<T> saveConsumer = null;
    @Nullable
    private Consumer<T> changeConsumer = null;
    private Function<T, Optional<Text[]>> tooltipSupplier = (e) -> Optional.empty();
    private final T value;
    private final Class<T> clazz;
    @Nullable
    private Function<T, Text> enumNameProvider;

    public DynamicEnumBuilder(Text resetButtonKey, Text fieldNameKey, Class<T> clazz, T value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(value);
        this.value = value;
        this.clazz = clazz;
    }

    public DynamicEnumBuilder<T> setErrorSupplier(Function<T, Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public DynamicEnumBuilder<T> requireRestart() {
        this.requireRestart(true);
        return this;
    }

    public DynamicEnumBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public DynamicEnumBuilder<T> setChangeConsumer(Consumer<T> changeConsumer) {
        this.changeConsumer = changeConsumer;
        return this;
    }

    public DynamicEnumBuilder<T> setDefaultValue(Supplier<T> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DynamicEnumBuilder<T> setDefaultValue(T defaultValue) {
        Objects.requireNonNull(defaultValue);
        this.defaultValue = () -> {
            return defaultValue;
        };
        return this;
    }

    public DynamicEnumBuilder<T> setTooltipSupplier(Function<T, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public DynamicEnumBuilder<T> setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = (e) -> (Optional)tooltipSupplier.get();
        return this;
    }

    public DynamicEnumBuilder<T> setTooltip(Text... tooltip) {
        this.tooltipSupplier = (e) -> {
            return Optional.ofNullable(tooltip);
        };
        return this;
    }

    public DynamicEnumBuilder<T> setEnumNameProvider(Function<T, Text> enumNameProvider) {
        this.enumNameProvider = enumNameProvider;
        return this;
    }

    @NotNull
    public DynamicEnumEntry<T> build() {
        DynamicEnumEntry<T> entry = new DynamicEnumEntry<T>(this.getFieldNameKey(), this.clazz, this.value, this.getResetButtonKey(), this.defaultValue, this.saveConsumer, this.changeConsumer, this.enumNameProvider, null, this.isRequireRestart());
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null)
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        return entry;
    }
}
