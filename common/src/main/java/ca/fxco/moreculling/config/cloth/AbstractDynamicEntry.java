package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractDynamicEntry<T> extends TooltipListEntry<T> {
    protected AbstractWidget mainWidget;
    protected Button resetButton;
    @Nullable
    private final Consumer<T> saveConsumer;
    @Nullable
    private final BiConsumer<AbstractDynamicEntry<T>, T> changeConsumer;

    private final T original;
    private final AtomicReference<T> value;
    private final Supplier<T> defaultValue;

    private final List<AbstractWidget> widgets;

    private boolean enabled;
    private final boolean locked;

    public AbstractDynamicEntry(AbstractDynamicBuilder<T, AbstractConfigListEntry<T>, ?> builder) {
        this(builder.getFieldNameKey(), builder.getResetButtonKey(), builder.getValue(), builder.getDefaultValue(), builder.saveConsumer, builder.changeConsumer, null, builder.isRequireRestart(), builder.getLocked());
    }

    public AbstractDynamicEntry(Component fieldName, Component resetButtonKey, T value, Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, @Nullable BiConsumer<AbstractDynamicEntry<T>, T> changeConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart, boolean locked) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = value;
        this.locked = locked;
        this.enabled = !locked;
        this.value = new AtomicReference<>(value);
        this.changeConsumer = changeConsumer;
        this.saveConsumer = saveConsumer;
        this.mainWidget = this.createMainWidget();
        this.resetButton = Button.builder(resetButtonKey, (widget) -> {
            if (this.getDefaultValue().isPresent() && !this.getValue().equals(this.getDefaultValue().get())) {
                this.setValue(this.getDefaultValue().get());
                this.onChange();
            }
        }).bounds(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20).build();
        this.widgets = Lists.newArrayList(this.mainWidget, this.resetButton);
    }

    public void onChange() {
        if (this.changeConsumer != null) {
            this.changeConsumer.accept(this, this.getValue());
        }
    }

    public final void save() {
        if (this.saveConsumer != null) {
            this.saveConsumer.accept(this.getValue());
        }
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final boolean isLocked() {
        return this.locked;
    }

    public boolean isEdited() {
        return super.isEdited() || this.original != this.getValue();
    }

    public void setEnabledState(boolean active) {
        if (!this.locked) {
            this.enabled = active;
            this.mainWidget.active = active;
            this.resetButton.active = active;
            this.onChange();
        }
    }

    public BiConsumer<AbstractDynamicEntry<T>, T> getChangeConsumer() {
        return this.changeConsumer;
    }

    public T getValue() {
        return this.value.get();
    }

    public void setValue(T value) {
        if (!this.locked && this.enabled) {
            this.value.set(value);
        }
    }

    public T getOriginal() {
        return this.original;
    }

    public final Optional<T> getDefaultValue() {
        return this.defaultValue == null ? Optional.empty() : Optional.ofNullable(this.defaultValue.get());
    }

    @Override
    public final void render(GuiGraphics drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(drawContext, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        this.resetButton.active = this.isEnabled() && this.isEditable() && this.getDefaultValue().isPresent() && !this.getDefaultValue().get().equals(this.getValue());
        this.resetButton.setY(y);
        this.mainWidget.active = this.isEnabled() && this.isEditable();
        this.mainWidget.setY(y);

        this.onRender(drawContext, y, x, entryWidth, entryHeight);

        this.resetButton.render(drawContext, mouseX, mouseY, delta);
        this.mainWidget.render(drawContext, mouseX, mouseY, delta);
    }

    // Create the main widget to use for this entry
    abstract AbstractWidget createMainWidget();

    // This is where you render your widgets & text
    abstract void onRender(GuiGraphics drawContext, int y, int x, int entryWidth, int entryHeight);

    @Override
    public Component getDisplayedFieldName() {
        MutableComponent text = this.getFieldName().copy();
        boolean hasError = this.getConfigError().isPresent();
        boolean isEdited = this.isEdited();
        boolean notEnabled = !isEnabled();
        if (hasError) {
            text = text.withStyle(ChatFormatting.RED);
        }
        if (isEdited) {
            text = text.withStyle(ChatFormatting.ITALIC);
        }
        if ((!hasError && !isEdited) || notEnabled) {
            text = text.withStyle(ChatFormatting.GRAY);
        }
        if (notEnabled) {
            text = text.withStyle(ChatFormatting.STRIKETHROUGH);
        }
        return text;
    }

    public List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    public List<? extends NarratableEntry> narratables() {
        return this.widgets;
    }
}
