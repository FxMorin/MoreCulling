package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DynamicEnumEntry<T extends Enum<?>> extends AbstractDynamicEntry<T> {

    private final ImmutableList<T> values;
    private final AtomicInteger index = new AtomicInteger();
    private final Function<T, Component> nameProvider;

    public DynamicEnumEntry(DynamicEnumBuilder<T> builder, Class<T> clazz, @Nullable Function<T, Component> nameProvider) {
        super(builder.getFieldNameKey(), builder.getResetButtonKey(), builder.getValue(), builder.getDefaultValue(), builder.saveConsumer, builder.changeConsumer, null, builder.isRequireRestart(), builder.getLocked());
        T[] enums = clazz.getEnumConstants();
        if (enums != null) {
            this.values = ImmutableList.copyOf(enums);
        } else {
            this.values = ImmutableList.of(builder.getValue());
        }
        this.nameProvider = nameProvider == null ? (t) -> {
            return Component.translatable(t instanceof SelectionListEntry.Translatable ?
                    ((SelectionListEntry.Translatable) t).getKey() : t.toString());
        } : nameProvider;
        this.setValue(builder.getValue());
        this.onChange(); // Run once on load
    }

    public boolean isEdited() {
        return (super.isEdited() || !Objects.equals(this.index.get(), this.values.indexOf(this.getOriginal())));
    }

    public void setValue(T value) {
        if (!this.isLocked() && this.isEnabled()) {
            this.index.set(this.values.indexOf(value));
        }
    }

    public T getValue() {
        return this.values.get(this.index.get());
    }

    @Override
    protected AbstractWidget createMainWidget() {
        return Button.builder(GameNarrator.NO_TITLE, (widget) -> {
            if (!this.isLocked() && this.isEnabled()) {
                this.index.incrementAndGet();
                this.index.compareAndSet(this.values.size(), 0);
                this.onChange();
            }
        }).bounds(0, 0, 150, 20).build();
    }

    @Override
    protected void onRender(GuiGraphics drawContext, int y, int x, int entryWidth, int entryHeight) {
        Window window = Minecraft.getInstance().getWindow();
        this.mainWidget.setMessage(this.nameProvider.apply(this.getValue()));
        Component displayedFieldName = this.getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 2);
        } else {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }

        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    private int getDefaultIndex() {
        return Math.max(0, this.values.indexOf(this.getDefaultValue().orElse(this.getValue())));
    }
}