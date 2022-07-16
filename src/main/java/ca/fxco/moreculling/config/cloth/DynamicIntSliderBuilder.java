package ca.fxco.moreculling.config.cloth;

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicIntSliderBuilder extends FieldBuilder<Integer, DynamicIntSliderEntry> {
    @Nullable
    private Consumer<Integer> saveConsumer = null;
    @Nullable
    private Consumer<Boolean> changeConsumer = null;
    private Function<Integer, Optional<Text[]>> tooltipSupplier = (i) -> Optional.empty();
    private final int value;
    private int max;
    private int min;
    private Function<Integer, Text> textGetter = null;

    public DynamicIntSliderBuilder(Text resetButtonKey, Text fieldNameKey, int value, int min, int max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
    }

    public DynamicIntSliderBuilder setErrorSupplier(Function<Integer, Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public DynamicIntSliderBuilder requireRestart() {
        this.requireRestart(true);
        return this;
    }

    public DynamicIntSliderBuilder setTextGetter(Function<Integer, Text> textGetter) {
        this.textGetter = textGetter;
        return this;
    }

    public DynamicIntSliderBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public DynamicIntSliderBuilder setChangeConsumer(Consumer<Boolean> changeConsumer) {
        this.changeConsumer = changeConsumer;
        return this;
    }

    public DynamicIntSliderBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DynamicIntSliderBuilder setDefaultValue(int defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }

    public DynamicIntSliderBuilder setTooltipSupplier(Function<Integer, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public DynamicIntSliderBuilder setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = (i) -> (Optional<Text[]>)tooltipSupplier.get();
        return this;
    }

    public DynamicIntSliderBuilder setTooltip(Text... tooltip) {
        this.tooltipSupplier = (i) -> Optional.ofNullable(tooltip);
        return this;
    }

    public DynamicIntSliderBuilder setMax(int max) {
        this.max = max;
        return this;
    }

    public DynamicIntSliderBuilder setMin(int min) {
        this.min = min;
        return this;
    }

    @NotNull
    public DynamicIntSliderEntry build() {
        DynamicIntSliderEntry entry = new DynamicIntSliderEntry(this.getFieldNameKey(), this.min, this.max, this.value, this.getResetButtonKey(), this.defaultValue, this.saveConsumer, this.changeConsumer, null, this.isRequireRestart());
        if (this.textGetter != null) entry.setTextGetter(this.textGetter);
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        return entry;
    }
}
