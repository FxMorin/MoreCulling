package ca.fxco.moreculling.config.cloth;

import ca.fxco.moreculling.utils.CacheUtils;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;

import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

public class MoreCullingClothConfigScreen extends ClothConfigScreen {

    private AbstractWidget resetCacheButton;

    public MoreCullingClothConfigScreen(Screen parent, Component title, Map<String,
            ConfigCategory> categoryMap, Identifier backgroundLocation) {
        super(parent, title, categoryMap, backgroundLocation);
    }

    @Override
    protected void init() {
        super.init();
        if (IS_MODERNFIX_LOADED) {
            return;
        }
        int buttonWidths = Math.min(200, (this.width - 50 - 12) / 4);
        this.addRenderableWidget(this.resetCacheButton = Button.builder(
                Component.translatable("moreculling.config.resetCache"),
                (widget) -> {
                    CacheUtils.resetAllCache();
                    this.resetCacheButton.active = false;
                }).bounds(10, 5, buttonWidths, 20).build()
        );
    }

    @Override
    public boolean isTransparentBackground() {
        return true;
    }
}
