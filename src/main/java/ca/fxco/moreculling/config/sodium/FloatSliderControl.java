package ca.fxco.moreculling.config.sodium;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class FloatSliderControl implements Control<Float> {
    private final Option<Float> option;

    private final float min, max, interval;

    private final String text;

    public FloatSliderControl(Option<Float> option, float min, float max, float interval, Text text) {
        Validate.isTrue(max > min, "The maximum value must be greater than the minimum value");
        Validate.isTrue(interval > 0, "The slider interval must be greater than zero");
        Validate.isTrue(((max - min) / interval) % 1 == 0, "The maximum value must be divisable by the interval");
        Validate.notNull(text, "The slider mode must not be null");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.text = text.getString();
    }

    @Override
    public ControlElement<Float> createElement(Dim2i dim) {
        return new Button(this.option, dim, this.min, this.max, this.interval, this.text);
    }

    @Override
    public Option<Float> getOption() {
        return this.option;
    }

    @Override
    public int getMaxWidth() {
        return 130;
    }

    private static class Button extends ControlElement<Float> {
        private static final int THUMB_WIDTH = 2, TRACK_HEIGHT = 1;

        private final Rect2i sliderBounds;

        private final float min;
        private final float range;
        private final float interval;

        private final String text;

        private double thumbPosition;

        public Button(Option<Float> option, Dim2i dim, float min, float max, float interval, String text) {
            super(option, dim);

            this.min = min;
            this.range = max - min;
            this.interval = interval;
            this.thumbPosition = this.getThumbPositionForValue(option.getValue());

            this.text = text;

            this.sliderBounds = new Rect2i(dim.getLimitX() - 96, dim.getCenterY() - 5, 90, 10);
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            super.render(drawContext, mouseX, mouseY, delta);

            if (this.option.isAvailable() && this.hovered) {
                this.renderSlider(drawContext);
            } else {
                this.renderStandaloneValue(drawContext);
            }
        }

        private void renderStandaloneValue(DrawContext drawContext) {
            int sliderX = this.sliderBounds.getX();
            int sliderY = this.sliderBounds.getY();
            int sliderWidth = this.sliderBounds.getWidth();
            int sliderHeight = this.sliderBounds.getHeight();

            String label = this.text.formatted(this.option.getValue());
            int labelWidth = this.font.getWidth(label);

            this.drawString(drawContext, label, sliderX + sliderWidth - labelWidth, sliderY + (sliderHeight / 2) - 4, 0xFFFFFFFF);
        }

        private void renderSlider(DrawContext drawContext) {
            int sliderX = this.sliderBounds.getX();
            int sliderY = this.sliderBounds.getY();
            int sliderWidth = this.sliderBounds.getWidth();
            int sliderHeight = this.sliderBounds.getHeight();

            this.thumbPosition = this.getThumbPositionForValue(option.getValue());

            double thumbOffset = MathHelper.clamp((double) (this.getFloatValue() - this.min) / this.range * sliderWidth, 0, sliderWidth);

            double thumbX = sliderX + thumbOffset - THUMB_WIDTH;
            double trackY = (float) sliderY + ((float) sliderHeight / 2) - ((double) TRACK_HEIGHT / 2);

            drawContext.fill((int) thumbX, sliderY, (int) thumbX + (THUMB_WIDTH * 2), sliderY + sliderHeight, 0xFFFFFFFF);
            drawContext.fill(sliderX, (int) trackY, sliderX + sliderWidth, (int) trackY + TRACK_HEIGHT, 0xFFFFFFFF);

            String label = String.valueOf(this.getFloatValue());

            int labelWidth = this.font.getWidth(label);

            this.drawString(drawContext, label, sliderX - labelWidth - 6, sliderY + (sliderHeight / 2) - 4, 0xFFFFFFFF);
        }

        public float getFloatValue() {
            return this.min + (this.interval * (int) Math.round(this.getSnappedThumbPosition() / this.interval));
        }

        public double getSnappedThumbPosition() {
            return this.thumbPosition / (1.0D / this.range);
        }

        public double getThumbPositionForValue(float value) {
            return (value - this.min) * (1.0D / this.range);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.option.isAvailable() && button == 0 && this.sliderBounds.contains((int) mouseX, (int) mouseY)) {
                this.setValueFromMouse(mouseX);
                return true;
            }
            return false;
        }

        private void setValueFromMouse(double d) {
            this.setValue((d - (double) this.sliderBounds.getX()) / (double) this.sliderBounds.getWidth());
        }

        private void setValue(double d) {
            this.thumbPosition = MathHelper.clamp(d, 0.0D, 1.0D);

            float value = this.getFloatValue();

            if (this.option.getValue() != value) {
                this.option.setValue(value);
            }
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (this.option.isAvailable() && button == 0) {
                this.setValueFromMouse(mouseX);
                return true;
            }
            return false;
        }
    }
}
