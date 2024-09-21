package ca.fxco.moreculling.config.cloth;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicBooleanListEntry extends AbstractDynamicEntry<Boolean> {

    public DynamicBooleanListEntry(Component fieldName, Component resetButtonKey, Boolean value, Supplier<Boolean> defaultValue, @Nullable Consumer<Boolean> saveConsumer, @Nullable BiConsumer<AbstractDynamicEntry<Boolean>, Boolean> changeConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart, boolean locked) {
        super(fieldName, resetButtonKey, value, defaultValue, saveConsumer, changeConsumer, tooltipSupplier, requiresRestart, locked);
        this.onChange(); // Run once on load
    }

    @Override
    public void onChange() {
        if (this.getChangeConsumer() != null) {
            this.getChangeConsumer().accept(this, this.isEnabled() && this.getValue());
        }
    }

    @Override
    protected AbstractWidget createMainWidget() {
        return Button.builder(GameNarrator.NO_TITLE, (widget) -> {
            if (this.isEnabled()) {
                this.setValue(!this.getValue());
                this.onChange();
            }
        }).bounds(0, 0, 150, 20).build();
    }

    @Override
    protected void onRender(GuiGraphics drawContext, int y, int x, int entryWidth, int entryHeight) {
        Window window = Minecraft.getInstance().getWindow();
        this.mainWidget.setMessage(this.getYesNoText(this.getValue()));
        Component displayedFieldName = this.getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, 16777215);
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 2);
        } else {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }
        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    public Component getYesNoText(boolean bool) {
        return Component.translatable("text.cloth-config.boolean.value." + bool);
    }
}
