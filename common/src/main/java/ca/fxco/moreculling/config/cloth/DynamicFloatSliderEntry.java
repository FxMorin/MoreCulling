package ca.fxco.moreculling.config.cloth;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class DynamicFloatSliderEntry extends AbstractDynamicEntry<Float> {
    private final float minimum;
    private final float maximum;
    private final float step;
    private Function<Float, Component> textGetter;

    public DynamicFloatSliderEntry(DynamicFloatSliderBuilder builder, float minimum, float maximum, float step) {
        super(builder.getFieldNameKey(), builder.getResetButtonKey(), builder.getValue(), builder.getDefaultValue(), builder.saveConsumer, builder.changeConsumer, null, builder.isRequireRestart(), builder.getLocked());
        this.textGetter = (thevalue) -> Component.literal(String.format("Value: %3.1f", thevalue));
        this.step = step;
        this.maximum = maximum;
        this.minimum = minimum;
        this.mainWidget.setMessage(this.textGetter.apply(this.getValue()));
        this.setValue(this.getValue());
        this.onChange(); // Run once on load
    }

    private static float roundStep(float input, float step) {
        return ((Math.round(input / step)) * step);
    }

    public Function<Float, Component> getTextGetter() {
        return this.textGetter;
    }

    public void setTextGetter(Function<Float, Component> textGetter) {
        this.textGetter = textGetter;
        this.mainWidget.setMessage(textGetter.apply(this.getValue()));
    }

    @Override
    public void setValue(Float value) {
        if (!this.isLocked() && this.isEnabled()) {
            value = roundStep(value, this.step);
            ((Slider) this.mainWidget).setValue((double) (Mth.clamp(value, this.minimum, this.maximum) - this.minimum) / (double) Math.abs(this.maximum - this.minimum));
            value = Math.min(Math.max(value, this.minimum), this.maximum);
            super.setValue(value);
            ((Slider) this.mainWidget).updateMessage();
        }
    }

    @Override
    protected AbstractWidget createMainWidget() {
        return new Slider(0, 0, 152, 20, ((double) this.getValue() - (double) this.minimum) / (double) Math.abs(this.maximum - this.minimum));
    }

    @Override
    protected void onRender(GuiGraphics drawContext, int y, int x, int entryWidth, int entryHeight) {
        Window window = Minecraft.getInstance().getWindow();
        Component displayedFieldName = this.getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x);
            this.mainWidget.setX(x + this.resetButton.getWidth() + 1);
        } else {
            drawContext.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.mainWidget.setX(x + entryWidth - 150);
        }
        this.mainWidget.setWidth(150 - this.resetButton.getWidth() - 2);
    }

    private class Slider extends AbstractSliderButton {
        protected Slider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, GameNarrator.NO_TITLE, value);
        }

        public void updateMessage() {
            this.setMessage(DynamicFloatSliderEntry.this.textGetter.apply(DynamicFloatSliderEntry.this.getValue()));
        }

        protected void applyValue() {
            DynamicFloatSliderEntry.this.setValue(roundStep((float) (DynamicFloatSliderEntry.this.minimum + Math.abs(DynamicFloatSliderEntry.this.maximum - DynamicFloatSliderEntry.this.minimum) * this.value), DynamicFloatSliderEntry.this.step));
        }

        public boolean keyPressed(int int_1, int int_2, int int_3) {
            return DynamicFloatSliderEntry.this.isEnabled() && (DynamicFloatSliderEntry.this.isEditable() && super.keyPressed(int_1, int_2, int_3));
        }

        public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
            return DynamicFloatSliderEntry.this.isEnabled() && (DynamicFloatSliderEntry.this.isEditable() && super.mouseDragged(double_1, double_2, int_1, double_3, double_4));
        }

        public double getProgress() {
            return this.value;
        }

        public void setProgress(double f) {
            this.value = f;
        }

        public void setValue(double f) {
            this.value = f;
        }
    }
}