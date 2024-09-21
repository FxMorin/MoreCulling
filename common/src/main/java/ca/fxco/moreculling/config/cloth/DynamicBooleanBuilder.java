package ca.fxco.moreculling.config.cloth;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DynamicBooleanBuilder extends AbstractDynamicBuilder<Boolean, DynamicBooleanListEntry, DynamicBooleanBuilder> {
    @Nullable
    private Function<Boolean, Component> yesNoTextSupplier = null;

    public DynamicBooleanBuilder(String translationKey) {
        super(translationKey);
    }

    public DynamicBooleanBuilder(String translationKey, Component resetButtonKey) {
        super(translationKey, resetButtonKey);
    }

    @Nullable
    public Function<Boolean, Component> getYesNoTextSupplier() {
        return this.yesNoTextSupplier;
    }

    public DynamicBooleanBuilder setYesNoTextSupplier(@Nullable Function<Boolean, Component> yesNoTextSupplier) {
        this.yesNoTextSupplier = yesNoTextSupplier;
        return this;
    }

    @NotNull
    public DynamicBooleanListEntry runBuild() {
        DynamicBooleanListEntry entry = new DynamicBooleanListEntry(this.getFieldNameKey(), this.getResetButtonKey(), this.getValue(), this.defaultValue, this.saveConsumer, this.changeConsumer, null, this.isRequireRestart(), this.getLocked()) {
            public Component getYesNoText(boolean bool) {
                return DynamicBooleanBuilder.this.yesNoTextSupplier == null ? super.getYesNoText(bool) : DynamicBooleanBuilder.this.yesNoTextSupplier.apply(bool);
            }
        };
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }
        return entry;
    }
}
