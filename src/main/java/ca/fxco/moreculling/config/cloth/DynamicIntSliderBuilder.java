package ca.fxco.moreculling.config.cloth;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class DynamicIntSliderBuilder extends AbstractDynamicBuilder<Integer, DynamicIntSliderEntry, DynamicIntSliderBuilder> {
    private int max;
    private int min;
    private Function<Integer, Component> textGetter = null;

    public DynamicIntSliderBuilder(String translationKey, int min, int max) {
        super(translationKey);
        this.max = max;
        this.min = min;
    }

    public DynamicIntSliderBuilder(String translationKey, Component resetButtonKey, int min, int max) {
        super(translationKey, resetButtonKey);
        this.max = max;
        this.min = min;
    }

    public DynamicIntSliderBuilder setTextGetter(Function<Integer, Component> textGetter) {
        this.textGetter = textGetter;
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
    public DynamicIntSliderEntry runBuild() {
        DynamicIntSliderEntry entry = new DynamicIntSliderEntry(this, this.min, this.max);
        if (this.textGetter != null) {
            entry.setTextGetter(this.textGetter);
        }
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }
        return entry;
    }
}
