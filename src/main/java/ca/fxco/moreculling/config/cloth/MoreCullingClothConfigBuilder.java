package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class MoreCullingClothConfigBuilder implements ConfigBuilder {
    private Runnable savingRunnable;
    private Screen parent;
    private Component title = Component.translatable("moreculling.title");
    private boolean editable = true;
    private boolean tabsSmoothScroll = true;
    private boolean listSmoothScroll = true;
    private boolean doesConfirmSave = true;
    private boolean transparentBackground = false;
    private ResourceLocation defaultBackground;
    private Consumer<Screen> afterInitConsumer;
    protected final Map<String, ConfigCategory> categoryMap;
    private String fallbackCategory;
    private boolean alwaysShowTabs;

    public static ConfigBuilder create() {
        return new MoreCullingClothConfigBuilder();
    }

    @ApiStatus.Internal
    public MoreCullingClothConfigBuilder() {
        this.defaultBackground = Screen.MENU_BACKGROUND;
        this.afterInitConsumer = (screen) -> {
        };
        this.categoryMap = Maps.newLinkedHashMap();
        this.fallbackCategory = null;
        this.alwaysShowTabs = false;
    }

    public boolean isAlwaysShowTabs() {
        return this.alwaysShowTabs;
    }

    public ConfigBuilder setAlwaysShowTabs(boolean alwaysShowTabs) {
        this.alwaysShowTabs = alwaysShowTabs;
        return this;
    }

    public ConfigBuilder setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
        return this;
    }

    public boolean hasTransparentBackground() {
        return this.transparentBackground;
    }

    public ConfigBuilder setAfterInitConsumer(Consumer<Screen> afterInitConsumer) {
        this.afterInitConsumer = afterInitConsumer;
        return this;
    }

    /**
     * MoreCulling config currently does not support globalized
     */
    @Override
    public void setGlobalized(boolean b) {
    }

    /**
     * MoreCulling config currently does not support globalized
     */
    @Override
    public void setGlobalizedExpanded(boolean b) {
    }

    public ConfigBuilder setFallbackCategory(ConfigCategory fallbackCategory) {
        this.fallbackCategory = Objects.requireNonNull(fallbackCategory).getCategoryKey().getString();
        return this;
    }

    public Screen getParentScreen() {
        return this.parent;
    }

    public ConfigBuilder setParentScreen(Screen parent) {
        this.parent = parent;
        return this;
    }

    public Component getTitle() {
        return this.title;
    }

    public ConfigBuilder setTitle(Component title) {
        this.title = title;
        return this;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public ConfigBuilder setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public ConfigCategory getOrCreateCategory(Component categoryKey) {
        if (this.categoryMap.containsKey(categoryKey.getString())) {
            return this.categoryMap.get(categoryKey.getString());
        } else {
            if (this.fallbackCategory == null) {
                this.fallbackCategory = categoryKey.getString();
            }
            return this.categoryMap.computeIfAbsent(categoryKey.getString(), (key) ->
                    new MoreCullingClothConfigCategory(this, categoryKey)
            );
        }
    }

    public ConfigBuilder removeCategory(Component category) {
        if (this.categoryMap.containsKey(category.getString()) &&
                Objects.equals(this.fallbackCategory, category.getString())) {
            this.fallbackCategory = null;
        }
        if (!this.categoryMap.containsKey(category.getString())) {
            throw new NullPointerException("Category doesn't exist!");
        } else {
            this.categoryMap.remove(category.getString());
            return this;
        }
    }

    public ConfigBuilder removeCategoryIfExists(Component category) {
        if (this.categoryMap.containsKey(category.getString()) &&
                Objects.equals(this.fallbackCategory, category.getString())) {
            this.fallbackCategory = null;
        }
        this.categoryMap.remove(category.getString());
        return this;
    }

    public boolean hasCategory(Component category) {
        return this.categoryMap.containsKey(category.getString());
    }

    public ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll) {
        this.tabsSmoothScroll = shouldTabsSmoothScroll;
        return this;
    }

    public boolean isTabsSmoothScrolling() {
        return this.tabsSmoothScroll;
    }

    public ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll) {
        this.listSmoothScroll = shouldListSmoothScroll;
        return this;
    }

    public boolean isListSmoothScrolling() {
        return this.listSmoothScroll;
    }

    public ConfigBuilder setDoesConfirmSave(boolean confirmSave) {
        this.doesConfirmSave = confirmSave;
        return this;
    }

    public boolean doesConfirmSave() {
        return this.doesConfirmSave;
    }

    public ResourceLocation getDefaultBackgroundTexture() {
        return this.defaultBackground;
    }

    public ConfigBuilder setDefaultBackgroundTexture(ResourceLocation texture) {
        this.defaultBackground = texture;
        return this;
    }

    public ConfigBuilder setSavingRunnable(Runnable runnable) {
        this.savingRunnable = runnable;
        return this;
    }

    public Consumer<Screen> getAfterInitConsumer() {
        return this.afterInitConsumer;
    }

    public Screen build() {
        if (!this.categoryMap.isEmpty() && this.fallbackCategory != null) {
            MoreCullingClothConfigScreen screen = new MoreCullingClothConfigScreen(
                    this.parent, this.title, this.categoryMap, this.defaultBackground
            );
            screen.setSavingRunnable(this.savingRunnable);
            screen.setEditable(this.editable);
            screen.setFallbackCategory(this.fallbackCategory == null ? null : Component.literal(this.fallbackCategory));
            screen.setTransparentBackground(this.transparentBackground);
            screen.setAlwaysShowTabs(this.alwaysShowTabs);
            screen.setConfirmSave(this.doesConfirmSave);
            screen.setAfterInitConsumer(this.afterInitConsumer);
            return screen;
        } else {
            throw new NullPointerException("There cannot be no categories or fallback category!");
        }
    }

    public Runnable getSavingRunnable() {
        return this.savingRunnable;
    }
}
