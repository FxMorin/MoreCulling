package ca.fxco.moreculling.config.cloth;

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicFloatSliderBuilder extends FieldBuilder<Float, DynamicFloatSliderEntry> {
    @Nullable
    private Consumer<Float> saveConsumer = null;
    @Nullable
    private Consumer<Boolean> changeConsumer = null;
    @NotNull
    private Function<Float, Optional<Text[]>> tooltipSupplier = (i) -> Optional.empty();
    private final float value;
    private float max;
    private float min;
    private float step;
    @Nullable
    private Function<Float, Text> textGetter = null;

    public DynamicFloatSliderBuilder(Text resetButtonKey, Text fieldNameKey, float value, float min, float max) {
        this(resetButtonKey, fieldNameKey, value, min, max, 0.5F);
    }

    public DynamicFloatSliderBuilder(Text resetButtonKey, Text fieldNameKey, float value, float min, float max, float step) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
        this.step = step;
    }

    public DynamicFloatSliderBuilder setErrorSupplier(Function<Float, Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public DynamicFloatSliderBuilder requireRestart() {
        this.requireRestart(true);
        return this;
    }

    public DynamicFloatSliderBuilder setTextGetter(Function<Float, Text> textGetter) {
        this.textGetter = textGetter;
        return this;
    }

    public DynamicFloatSliderBuilder setSaveConsumer(Consumer<Float> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public DynamicFloatSliderBuilder setChangeConsumer(Consumer<Boolean> changeConsumer) {
        this.changeConsumer = changeConsumer;
        return this;
    }

    public DynamicFloatSliderBuilder setDefaultValue(Supplier<Float> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DynamicFloatSliderBuilder setDefaultValue(float defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }

    public DynamicFloatSliderBuilder setTooltipSupplier(Function<Float, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public DynamicFloatSliderBuilder setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = (i) -> (Optional<Text[]>)tooltipSupplier.get();
        return this;
    }

    public DynamicFloatSliderBuilder setTooltip(Text... tooltip) {
        this.tooltipSupplier = (i) -> Optional.ofNullable(tooltip);
        return this;
    }

    public DynamicFloatSliderBuilder setMax(float max) {
        this.max = max;
        return this;
    }

    public DynamicFloatSliderBuilder setMin(float min) {
        this.min = min;
        return this;
    }

    public DynamicFloatSliderBuilder setStep(float step) {
        this.step = step;
        return this;
    }

    @NotNull
    public DynamicFloatSliderEntry build() {
        DynamicFloatSliderEntry entry = new DynamicFloatSliderEntry(this.getFieldNameKey(), this.min, this.max, this.step, this.value, this.getResetButtonKey(), this.defaultValue, this.saveConsumer, this.changeConsumer, null, this.isRequireRestart());
        if (this.textGetter != null) entry.setTextGetter(this.textGetter);
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        return entry;
    }
}
