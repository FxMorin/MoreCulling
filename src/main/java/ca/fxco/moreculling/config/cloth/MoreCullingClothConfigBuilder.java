package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.data.client.TextureMap;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class MoreCullingClothConfigBuilder implements ConfigBuilder {
    private Runnable savingRunnable;
    private Screen parent;
    private Text title = Text.translatable("moreculling.title");
    private boolean editable = true;
    private boolean tabsSmoothScroll = true;
    private boolean listSmoothScroll = true;
    private boolean doesConfirmSave = true;
    private boolean transparentBackground = false;
    private Identifier defaultBackground;
    private Consumer<Screen> afterInitConsumer;
    protected final Map<String, ConfigCategory> categoryMap;
    private String fallbackCategory;
    private boolean alwaysShowTabs;

    public static ConfigBuilder create() {
        return new MoreCullingClothConfigBuilder();
    }

    @ApiStatus.Internal
    public MoreCullingClothConfigBuilder() {
        this.defaultBackground = Screen.OPTIONS_BACKGROUND_TEXTURE;
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

    public Text getTitle() {
        return this.title;
    }

    public ConfigBuilder setTitle(Text title) {
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

    public ConfigCategory getOrCreateCategory(Text categoryKey) {
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

    public ConfigBuilder removeCategory(Text category) {
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

    public ConfigBuilder removeCategoryIfExists(Text category) {
        if (this.categoryMap.containsKey(category.getString()) &&
                Objects.equals(this.fallbackCategory, category.getString())) {
            this.fallbackCategory = null;
        }
        this.categoryMap.remove(category.getString());
        return this;
    }

    public boolean hasCategory(Text category) {
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

    public Identifier getDefaultBackgroundTexture() {
        return this.defaultBackground;
    }

    public ConfigBuilder setDefaultBackgroundTexture(Identifier texture) {
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
            screen.setFallbackCategory(this.fallbackCategory == null ? null : Text.literal(this.fallbackCategory));
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
