package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicBooleanListEntry extends TooltipListEntry<Boolean> {

    /**
     * DynamicBooleanListEntry is just me.shedaniel.clothconfig2.gui.entries.BooleanListEntry except its dynamic so
     * that I can change the button states directly
     */

    private final AtomicBoolean bool;
    private final boolean original;
    private final ButtonWidget buttonWidget;
    private final ButtonWidget resetButton;
    private final Consumer<Boolean> saveConsumer;
    @Nullable
    private final Consumer<Boolean> changeConsumer;
    private final Supplier<Boolean> defaultValue;
    private final List<ClickableWidget> widgets;
    private boolean enabled;

    @ApiStatus.Internal
    public DynamicBooleanListEntry(Text fieldName, boolean bool, Text resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Consumer<Boolean> changeConsumer) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, changeConsumer, null);
    }

    @ApiStatus.Internal
    public DynamicBooleanListEntry(Text fieldName, boolean bool, Text resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Consumer<Boolean> changeConsumer, Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, changeConsumer, tooltipSupplier, false);
    }

    @SuppressWarnings("deprecation")
    public DynamicBooleanListEntry(Text fieldName, boolean bool, Text resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Consumer<Boolean> changeConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = bool;
        this.enabled = true; // Default true
        this.bool = new AtomicBoolean(bool);
        this.changeConsumer = changeConsumer;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, (widget) -> {
            if (this.enabled) {
                this.bool.set(!this.bool.get());
                if (this.changeConsumer != null)
                    this.changeConsumer.accept(this.bool.get());
            }
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, (widget) -> {
            if (this.enabled && this.bool.get() != defaultValue.get()) {
                this.bool.set(defaultValue.get());
                if (this.changeConsumer != null)
                    this.changeConsumer.accept(defaultValue.get());
            }
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(new ClickableWidget[]{this.buttonWidget, this.resetButton});
        if (this.changeConsumer != null) // Run once on load
            this.changeConsumer.accept(this.enabled && this.bool.get());
    }

    @Override
    public Text getDisplayedFieldName() {
        MutableText text = this.getFieldName().copy();
        boolean hasError = this.getConfigError().isPresent();
        boolean isEdited = this.isEdited();
        boolean notEnabled = !isEnabled();
        if (hasError) text = text.formatted(Formatting.RED);
        if (isEdited) text = text.formatted(Formatting.ITALIC);
        if ((!hasError && !isEdited) || notEnabled) text = text.formatted(Formatting.GRAY);
        if (notEnabled) text = text.formatted(Formatting.STRIKETHROUGH);
        return text;
    }

    public boolean isEdited() {
        return super.isEdited() || this.original != this.bool.get();
    }

    public void save() {
        if (this.saveConsumer != null)
            this.saveConsumer.accept(this.getValue());
    }

    public void setButtonState(boolean active) {
        this.enabled = active;
        this.buttonWidget.active = active;
        this.resetButton.active = active;
        if (this.changeConsumer != null)
            this.changeConsumer.accept(active);
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public Boolean getValue() {
        return this.bool.get();
    }

    public Optional<Boolean> getDefaultValue() {
        return this.defaultValue == null ? Optional.empty() : Optional.ofNullable(this.defaultValue.get());
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = this.isEnabled() && this.isEditable() && this.getDefaultValue().isPresent() && this.defaultValue.get() != this.bool.get();
        this.resetButton.y = y;
        this.buttonWidget.active = this.isEnabled() && this.isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(this.getYesNoText(this.bool.get()));
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)(window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (float)(y + 6), 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + this.resetButton.getWidth() + 2;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)x, (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.x = x + entryWidth - this.resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
        }

        this.buttonWidget.setWidth(150 - this.resetButton.getWidth() - 2);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
        this.buttonWidget.render(matrices, mouseX, mouseY, delta);
    }

    public Text getYesNoText(boolean bool) {
        return Text.translatable("text.cloth-config.boolean.value." + bool);
    }

    public List<? extends Element> children() {
        return this.widgets;
    }

    public List<? extends Selectable> narratables() {
        return this.widgets;
    }
}
