package ca.fxco.moreculling.config.cloth;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class DynamicIntSliderEntry extends AbstractDynamicEntry<Integer> {
    private final int minimum;
    private final int maximum;
    private Function<Integer, Text> textGetter;

    public DynamicIntSliderEntry(DynamicIntSliderBuilder builder, int minimum, int maximum) {
        super(builder.getFieldNameKey(), builder.getResetButtonKey(), builder.getValue(), builder.getDefaultValue(), builder.saveConsumer, builder.changeConsumer, null, builder.isRequireRestart(), builder.getLocked());
        this.textGetter = (integer) -> Text.literal(String.format("Value: %d", integer));
        this.maximum = maximum;
        this.minimum = minimum;
        this.mainWidget.setMessage(this.textGetter.apply(this.getValue()));
        this.setValue(this.getValue());
        this.onChange(); // Run once on load
    }

    public Function<Integer, Text> getTextGetter() {
        return this.textGetter;
    }

    public void setTextGetter(Function<Integer, Text> textGetter) {
        this.textGetter = textGetter;
        this.mainWidget.setMessage(textGetter.apply(this.getValue()));
    }

    @Override
    public void setValue(Integer value) {
        if (!this.isLocked() && this.isEnabled()) {
            ((Slider)this.mainWidget).setValue((double) (MathHelper.clamp(value, this.minimum, this.maximum) - this.minimum) / (double) Math.abs(this.maximum - this.minimum));
            value = Math.min(Math.max(value, this.minimum), this.maximum);
            super.setValue(value);
            ((Slider)this.mainWidget).updateMessage();
        }
    }

    @Override
    protected ClickableWidget createMainWidget() {
        return new Slider(0, 0, 152, 20, ((double)this.getValue() - (double)this.minimum) / (double)Math.abs(this.maximum - this.minimum));
    }

    @Override
    protected void onRender(MatrixStack matrices, int y, int x, int entryWidth, int entryHeight) {
        Window window = MinecraftClient.getInstance().getWindow();
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)(window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 1);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)x, (float)(y + 6), this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }
        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    private class Slider extends SliderWidget {
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, NarratorManager.EMPTY, double_1);
        }

        public void updateMessage() {
            this.setMessage(DynamicIntSliderEntry.this.textGetter.apply(DynamicIntSliderEntry.this.getValue()));
        }

        protected void applyValue() {
            DynamicIntSliderEntry.this.setValue((int) ((double) DynamicIntSliderEntry.this.minimum + (double) Math.abs(DynamicIntSliderEntry.this.maximum - DynamicIntSliderEntry.this.minimum) * this.value));
        }

        public boolean keyPressed(int int_1, int int_2, int int_3) {
            return DynamicIntSliderEntry.this.isEnabled() && (DynamicIntSliderEntry.this.isEditable() && super.keyPressed(int_1, int_2, int_3));
        }

        public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
            return DynamicIntSliderEntry.this.isEnabled() && (DynamicIntSliderEntry.this.isEditable() && super.mouseDragged(double_1, double_2, int_1, double_3, double_4));
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
