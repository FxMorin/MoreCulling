package ca.fxco.moreculling.config.cloth;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DynamicBooleanBuilder extends AbstractDynamicBuilder<Boolean, DynamicBooleanListEntry> {
    @Nullable
    private Function<Boolean, Text> yesNoTextSupplier = null;

    public DynamicBooleanBuilder(Text fieldNameKey) {
        super(fieldNameKey);
    }

    public DynamicBooleanBuilder(Text fieldNameKey, Text resetButtonKey) {
        super(fieldNameKey, resetButtonKey);
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
    public DynamicBooleanListEntry runBuild() {
        DynamicBooleanListEntry entry = new DynamicBooleanListEntry(this.getFieldNameKey(), this.getResetButtonKey(), this.getValue(), this.defaultValue, this.saveConsumer, this.changeConsumer, null, this.isRequireRestart(), this.getLocked()) {
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
