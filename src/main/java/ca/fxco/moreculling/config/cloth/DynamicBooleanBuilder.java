package ca.fxco.moreculling.config.cloth;

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicBooleanBuilder extends FieldBuilder<Boolean, DynamicBooleanListEntry> {
    @Nullable
    private Consumer<Boolean> saveConsumer = null;
    @Nullable
    private Consumer<Boolean> changeConsumer = null;
    @NotNull
    private Function<Boolean, Optional<Text[]>> tooltipSupplier = (bool) -> {
        return Optional.empty();
    };
    private final boolean value;
    @Nullable
    private Function<Boolean, Text> yesNoTextSupplier = null;

    public DynamicBooleanBuilder(Text resetButtonKey, Text fieldNameKey, boolean value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }

    public DynamicBooleanBuilder setErrorSupplier(@Nullable Function<Boolean, Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public DynamicBooleanBuilder requireRestart() {
        this.requireRestart(true);
        return this;
    }

    public DynamicBooleanBuilder setSaveConsumer(Consumer<Boolean> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public DynamicBooleanBuilder setChangeConsumer(Consumer<Boolean> changeConsumer) {
        this.changeConsumer = changeConsumer;
        return this;
    }

    public DynamicBooleanBuilder setDefaultValue(Supplier<Boolean> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DynamicBooleanBuilder setDefaultValue(boolean defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }

    public DynamicBooleanBuilder setTooltipSupplier(@NotNull Function<Boolean, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public DynamicBooleanBuilder setTooltipSupplier(@NotNull Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = (bool) -> (Optional<Text[]>)tooltipSupplier.get();
        return this;
    }

    public DynamicBooleanBuilder setTooltip(@Nullable Text... tooltip) {
        this.tooltipSupplier = (bool) -> Optional.ofNullable(tooltip);
        return this;
    }

    @Nullable
    public Function<Boolean, Text> getYesNoTextSupplier() {
        return this.yesNoTextSupplier;
    }

    public DynamicBooleanBuilder setYesNoTextSupplier(@Nullable Function<Boolean, Text> yesNoTextSupplier) {
        this.yesNoTextSupplier = yesNoTextSupplier;
        return this;
    }

    @NotNull
    public DynamicBooleanListEntry build() {
        DynamicBooleanListEntry entry = new DynamicBooleanListEntry(this.getFieldNameKey(), this.value, this.getResetButtonKey(), this.defaultValue, this.saveConsumer, this.changeConsumer, null, this.isRequireRestart()) {
            public Text getYesNoText(boolean bool) {
                return DynamicBooleanBuilder.this.yesNoTextSupplier == null ? super.getYesNoText(bool) : DynamicBooleanBuilder.this.yesNoTextSupplier.apply(bool);
            }
        };
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null)
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        return entry;
    }
}
