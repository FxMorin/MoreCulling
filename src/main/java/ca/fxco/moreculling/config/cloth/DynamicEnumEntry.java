package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DynamicEnumEntry<T extends Enum<?>> extends TooltipListEntry<T> {
    public static final Function<Enum<?>, Text> DEFAULT_NAME_PROVIDER = (t) -> {
        return Text.translatable(t instanceof SelectionListEntry.Translatable ?
                ((SelectionListEntry.Translatable)t).getKey() : t.toString());
    };

    private final ImmutableList<T> values;
    private final AtomicInteger index;
    private final int original;
    private final ButtonWidget buttonWidget;
    private final ButtonWidget resetButton;
    @Nullable
    private final Consumer<T> saveConsumer;
    @Nullable
    private final Consumer<T> changeConsumer;
    private final Supplier<T> defaultValue;
    private final List<ClickableWidget> widgets;
    private final Function<T, Text> nameProvider;
    private boolean enabled;

    @ApiStatus.Internal
    public DynamicEnumEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Consumer<T> changeConsumer) {
        this(fieldName, clazz, value, resetButtonKey, defaultValue, saveConsumer, changeConsumer, DEFAULT_NAME_PROVIDER::apply);
    }

    @ApiStatus.Internal
    public DynamicEnumEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Consumer<T> changeConsumer, Function<T, Text> nameProvider) {
        this(fieldName, clazz, value, resetButtonKey, defaultValue, saveConsumer, changeConsumer, nameProvider, null);
    }

    @ApiStatus.Internal
    public DynamicEnumEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Consumer<T> changeConsumer, Function<T, Text> nameProvider, Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, clazz, value, resetButtonKey, defaultValue, saveConsumer, changeConsumer, nameProvider, tooltipSupplier, false);
    }

    public DynamicEnumEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, @Nullable Consumer<T> changeConsumer, Function<T, Text> nameProvider, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        T[] enums = clazz.getEnumConstants();
        if (enums != null) {
            this.values = ImmutableList.copyOf(enums);
        } else {
            this.values = ImmutableList.of(value);
        }
        this.enabled = true; // Default true
        this.defaultValue = defaultValue;
        this.index = new AtomicInteger(this.values.indexOf(value));
        this.index.compareAndSet(-1, 0);
        this.original = this.values.indexOf(value);
        this.changeConsumer = changeConsumer;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, (widget) -> {
            if (this.enabled) {
                this.index.incrementAndGet();
                this.index.compareAndSet(this.values.size(), 0);
                if (this.changeConsumer != null)
                    this.changeConsumer.accept(this.getValue());
            }
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, (widget) -> {
            if (this.enabled) {
                this.index.set(this.getDefaultIndex());
                if (this.changeConsumer != null)
                    this.changeConsumer.accept(this.getDefaultValue().orElse(this.getValue()));
            }
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(new ClickableWidget[]{this.buttonWidget, this.resetButton});
        this.nameProvider = nameProvider == null ? (t) -> {
            return Text.translatable(t instanceof SelectionListEntry.Translatable ? ((SelectionListEntry.Translatable)t).getKey() : t.toString());
        } : nameProvider;
    }

    public void save() {
        if (this.saveConsumer != null) {
            this.saveConsumer.accept(this.getValue());
        }
    }

    public void setButtonState(boolean active) {
        this.enabled = active;
        this.buttonWidget.active = active;
        this.resetButton.active = active;
        if (this.changeConsumer != null)
            this.changeConsumer.accept(active ? this.getValue() : this.getDefaultValue().orElse(this.getValue()));
    }

    public boolean isEdited() {
        return (super.isEdited() || !Objects.equals(this.index.get(), this.original));
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setValue(T value) {
        this.index.set(this.values.indexOf(value));
    }

    public T getValue() {
        return this.values.get(this.index.get());
    }

    public Optional<T> getDefaultValue() {
        return this.defaultValue == null ? Optional.empty() : Optional.ofNullable(this.defaultValue.get());
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
        this.resetButton.active = this.isEnabled() && this.isEditable() && this.getDefaultValue().isPresent() && this.getDefaultIndex() != this.index.get();
        this.resetButton.y = y;
        this.buttonWidget.active = this.isEnabled() && this.isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(this.nameProvider.apply(this.getValue()));
        Text displayedFieldName = this.getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), (float)(window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (float)(y + 6), this.getPreferredTextColor());
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

    private int getDefaultIndex() {
        return Math.max(0, this.values.indexOf(this.defaultValue.get()));
    }

    public List<? extends Element> children() {
        return this.widgets;
    }

    public List<? extends Selectable> narratables() {
        return this.widgets;
    }

    public interface Translatable {
        @NotNull
        String getKey();
    }
}