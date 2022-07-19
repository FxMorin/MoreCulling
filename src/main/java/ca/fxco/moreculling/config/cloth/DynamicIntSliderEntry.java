package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicIntSliderEntry extends TooltipListEntry<Integer> {
    protected DynamicIntSliderEntry.Slider sliderWidget;
    protected ButtonWidget resetButton;
    protected AtomicInteger value;
    protected final long orginial;
    private int minimum;
    private int maximum;
    @Nullable
    private final Consumer<Integer> saveConsumer;
    @Nullable
    private final Consumer<Boolean> changeConsumer;
    private final Supplier<Integer> defaultValue;
    private Function<Integer, Text> textGetter;
    private final List<ClickableWidget> widgets;
    private boolean enabled;

    @ApiStatus.Internal
    public DynamicIntSliderEntry(Text fieldName, int minimum, int maximum, int value, Text resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Consumer<Boolean> changeConsumer) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, changeConsumer,null);
    }

    @ApiStatus.Internal
    public DynamicIntSliderEntry(Text fieldName, int minimum, int maximum, int value, Text resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Consumer<Boolean> changeConsumer, Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, changeConsumer, tooltipSupplier, false);
    }

    @ApiStatus.Internal
    public DynamicIntSliderEntry(Text fieldName, int minimum, int maximum, int value, Text resetButtonKey, Supplier<Integer> defaultValue, @Nullable Consumer<Integer> saveConsumer, @Nullable Consumer<Boolean> changeConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.enabled = true; // Default value
        this.textGetter = (integer) -> Text.literal(String.format("Value: %d", integer));
        this.orginial = value;
        this.defaultValue = defaultValue;
        this.value = new AtomicInteger(value);
        this.changeConsumer = changeConsumer;
        this.saveConsumer = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new DynamicIntSliderEntry.Slider(0, 0, 152, 20, ((double)this.value.get() - (double)minimum) / (double)Math.abs(maximum - minimum));
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, (widget) -> {
            this.setValue(this.defaultValue.get());
        });
        this.sliderWidget.setMessage(this.textGetter.apply(this.value.get()));
        this.widgets = Lists.newArrayList(this.sliderWidget, this.resetButton);
        if (this.changeConsumer != null) // Run once on load
            this.changeConsumer.accept(this.enabled);
    }

    public void save() {
        if (this.saveConsumer != null)
            this.saveConsumer.accept(this.getValue());
    }

    public void setSliderState(boolean active) {
        this.enabled = active;
        this.sliderWidget.active = active;
        this.resetButton.active = active;
        if (this.changeConsumer != null)
            this.changeConsumer.accept(active);
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public Function<Integer, Text> getTextGetter() {
        return this.textGetter;
    }

    public DynamicIntSliderEntry setTextGetter(Function<Integer, Text> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage((Text)textGetter.apply(this.value.get()));
        return this;
    }

    public Integer getValue() {
        return this.value.get();
    }

    public void setValue(int value) {
        if (this.enabled) {
            this.sliderWidget.setValue((double) (MathHelper.clamp(value, this.minimum, this.maximum) - this.minimum) / (double) Math.abs(this.maximum - this.minimum));
            value = Math.min(Math.max(value, this.minimum), this.maximum);
            this.value.set(value);
            this.sliderWidget.updateMessage();
        }
    }

    public boolean isEdited() {
        return super.isEdited() || (long)this.getValue() != this.orginial;
    }

    public Optional<Integer> getDefaultValue() {
        return this.defaultValue == null ? Optional.empty() : Optional.ofNullable((Integer)this.defaultValue.get());
    }

    public List<? extends Element> children() {
        return this.widgets;
    }

    public List<? extends Selectable> narratables() {
        return this.widgets;
    }

    public DynamicIntSliderEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    public DynamicIntSliderEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
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

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = this.isEnabled() && this.isEditable() && this.getDefaultValue().isPresent() && this.defaultValue.get() != this.value.get();
        this.resetButton.y = y;
        this.sliderWidget.active = this.isEnabled() && this.isEditable();
        this.sliderWidget.y = y;
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)(window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.x = x;
            this.sliderWidget.x = x + this.resetButton.getWidth() + 1;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)x, (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.x = x + entryWidth - this.resetButton.getWidth();
            this.sliderWidget.x = x + entryWidth - 150;
        }

        this.sliderWidget.setWidth(150 - this.resetButton.getWidth() - 2);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
        this.sliderWidget.render(matrices, mouseX, mouseY, delta);
    }

    private class Slider extends SliderWidget {
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, NarratorManager.EMPTY, double_1);
        }

        public void updateMessage() {
            this.setMessage(DynamicIntSliderEntry.this.textGetter.apply(DynamicIntSliderEntry.this.value.get()));
        }

        protected void applyValue() {
            if (DynamicIntSliderEntry.this.enabled)
                DynamicIntSliderEntry.this.value.set((int) ((double) DynamicIntSliderEntry.this.minimum + (double) Math.abs(DynamicIntSliderEntry.this.maximum - DynamicIntSliderEntry.this.minimum) * this.value));
        }

        public boolean keyPressed(int int_1, int int_2, int int_3) {
            return DynamicIntSliderEntry.this.enabled && (DynamicIntSliderEntry.this.isEditable() && super.keyPressed(int_1, int_2, int_3));
        }

        public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
            return DynamicIntSliderEntry.this.enabled && (DynamicIntSliderEntry.this.isEditable() && super.mouseDragged(double_1, double_2, int_1, double_3, double_4));
        }

        public double getProgress() {
            return this.value;
        }

        public void setProgress(double integer) {
            this.value = integer;
        }

        public void setValue(double integer) {
            this.value = integer;
        }
    }
}
