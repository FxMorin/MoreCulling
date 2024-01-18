package ca.fxco.moreculling.config.cloth;

import ca.fxco.moreculling.utils.CacheUtils;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

public class MoreCullingClothConfigScreen extends ClothConfigScreen {

    private ClickableWidget resetCacheButton;

    public MoreCullingClothConfigScreen(Screen parent, Text title, Map<String,
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
        this.addDrawableChild(this.resetCacheButton = ButtonWidget.builder(
                Text.translatable("moreculling.config.resetCache"),
                (widget) -> {
                    CacheUtils.resetAllCache();
                    this.resetCacheButton.active = false;
                }).dimensions(10, 5, buttonWidths, 20).build()
        );
    }
}
