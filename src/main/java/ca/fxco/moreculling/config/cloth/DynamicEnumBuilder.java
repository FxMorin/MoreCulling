package ca.fxco.moreculling.config.cloth;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DynamicEnumBuilder<T extends Enum<?>> extends AbstractDynamicBuilder<T, DynamicEnumEntry<T>, DynamicEnumBuilder<T>> {
    private final Class<T> clazz;
    @Nullable
    private Function<T, Text> enumNameProvider;

    public DynamicEnumBuilder(Text fieldNameKey, Class<T> clazz) {
        super(fieldNameKey);
        Objects.requireNonNull(clazz);
        this.clazz = clazz;
    }

    public DynamicEnumBuilder(Text fieldNameKey, Text resetButtonKey, Class<T> clazz) {
        super(fieldNameKey, resetButtonKey);
        Objects.requireNonNull(clazz);
        this.clazz = clazz;
    }

    public DynamicEnumBuilder<T> setEnumNameProvider(Function<T, Text> enumNameProvider) {
        this.enumNameProvider = enumNameProvider;
        return this;
    }

    @NotNull
    public DynamicEnumEntry<T> runBuild() {
        DynamicEnumEntry<T> entry = new DynamicEnumEntry<>(this, this.clazz, this.enumNameProvider);
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }
        return entry;
    }
}
