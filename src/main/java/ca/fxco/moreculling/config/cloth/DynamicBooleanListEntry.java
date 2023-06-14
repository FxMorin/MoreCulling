package ca.fxco.moreculling.config.cloth;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicBooleanListEntry extends AbstractDynamicEntry<Boolean> {

    public DynamicBooleanListEntry(Text fieldName, Text resetButtonKey, Boolean value, Supplier<Boolean> defaultValue, @Nullable Consumer<Boolean> saveConsumer, @Nullable BiConsumer<AbstractDynamicEntry<Boolean>, Boolean> changeConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart, boolean locked) {
        super(fieldName, resetButtonKey, value, defaultValue, saveConsumer, changeConsumer, tooltipSupplier, requiresRestart, locked);
        this.onChange(); // Run once on load
    }

    @Override
    public void onChange() {
        if (this.getChangeConsumer() != null)
            this.getChangeConsumer().accept(this, this.isEnabled() && this.getValue());
    }

    @Override
    protected ClickableWidget createMainWidget() {
        return ButtonWidget.builder(NarratorManager.EMPTY, (widget) -> {
            if (this.isEnabled()) {
                this.setValue(!this.getValue());
                this.onChange();
            }
        }).dimensions(0, 0, 150, 20).build();
    }

    @Override
    protected void onRender(DrawContext drawContext, int y, int x, int entryWidth, int entryHeight) {
        Window window = MinecraftClient.getInstance().getWindow();
        this.mainWidget.setMessage(this.getYesNoText(this.getValue()));
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, 16777215);
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 2);
        } else {
            drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), x, y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }
        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    public Text getYesNoText(boolean bool) {
        return Text.translatable("text.cloth-config.boolean.value." + bool);
    }
}
