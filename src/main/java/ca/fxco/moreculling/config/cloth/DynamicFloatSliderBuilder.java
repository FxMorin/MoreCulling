package ca.fxco.moreculling.config.cloth;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DynamicFloatSliderBuilder extends AbstractDynamicBuilder<Float, DynamicFloatSliderEntry> {
    private float max;
    private float min;
    private float step;
    @Nullable
    private Function<Float, Text> textGetter = null;

    public DynamicFloatSliderBuilder(Text fieldNameKey, float min, float max) {
        this(fieldNameKey, min, max, 0.5F);
    }

    public DynamicFloatSliderBuilder(Text fieldNameKey, Text resetButtonKey, float min, float max) {
        this(fieldNameKey, resetButtonKey, min, max, 0.5F);
    }

    public DynamicFloatSliderBuilder(Text fieldNameKey, float min, float max, float step) {
        super(fieldNameKey);
        this.max = max;
        this.min = min;
        this.step = step;
    }

    public DynamicFloatSliderBuilder(Text fieldNameKey, Text resetButtonKey, float min, float max, float step) {
        super(fieldNameKey, resetButtonKey);
        this.max = max;
        this.min = min;
        this.step = step;
    }

    public DynamicFloatSliderBuilder setTextGetter(Function<Float, Text> textGetter) {
        this.textGetter = textGetter;
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
    public DynamicFloatSliderEntry runBuild() {
        DynamicFloatSliderEntry entry = new DynamicFloatSliderEntry(this, this.min, this.max, this.step);
        if (this.textGetter != null) entry.setTextGetter(this.textGetter);
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        return entry;
    }
}
