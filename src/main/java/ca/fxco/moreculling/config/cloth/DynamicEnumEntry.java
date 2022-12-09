package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DynamicEnumEntry<T extends Enum<?>> extends AbstractDynamicEntry<T> {

    private final ImmutableList<T> values;
    private final AtomicInteger index = new AtomicInteger();
    private final Function<T, Text> nameProvider;

    public DynamicEnumEntry(DynamicEnumBuilder<T> builder, Class<T> clazz, @Nullable Function<T, Text> nameProvider) {
        super(builder.getFieldNameKey(), builder.getResetButtonKey(), builder.getValue(), builder.getDefaultValue(), builder.saveConsumer, builder.changeConsumer, null, builder.isRequireRestart(), builder.getLocked());
        T[] enums = clazz.getEnumConstants();
        if (enums != null) {
            this.values = ImmutableList.copyOf(enums);
        } else {
            this.values = ImmutableList.of(builder.getValue());
        }
        this.nameProvider = nameProvider == null ? (t) -> {
            return Text.translatable(t instanceof SelectionListEntry.Translatable ?
                    ((SelectionListEntry.Translatable)t).getKey() : t.toString());
        } : nameProvider;
        this.setValue(builder.getValue());
        this.onChange(); // Run once on load
    }

    public boolean isEdited() {
        return (super.isEdited() || !Objects.equals(this.index.get(), this.values.indexOf(this.getOriginal())));
    }

    public void setValue(T value) {
        if (!this.isLocked() && this.isEnabled())
            this.index.set(this.values.indexOf(value));
    }

    public T getValue() {
        return this.values.get(this.index.get());
    }

    @Override
    protected ClickableWidget createMainWidget() {
        return ButtonWidget.builder(NarratorManager.EMPTY, (widget) -> {
            if (!this.isLocked() && this.isEnabled()) {
                this.index.incrementAndGet();
                this.index.compareAndSet(this.values.size(), 0);
                this.onChange();
            }
        }).dimensions(0, 0, 150, 20).build();
    }

    @Override
    protected void onRender(MatrixStack matrices, int y, int x, int entryWidth, int entryHeight) {
        Window window = MinecraftClient.getInstance().getWindow();
        this.mainWidget.setMessage(this.nameProvider.apply(this.getValue()));
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)(window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 2);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)x, (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }

        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    private int getDefaultIndex() {
        return Math.max(0, this.values.indexOf(this.getDefaultValue().orElse(this.getValue())));
    }
}