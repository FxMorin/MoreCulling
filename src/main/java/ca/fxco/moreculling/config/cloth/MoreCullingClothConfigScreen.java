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

public class MoreCullingClothConfigScreen extends ClothConfigScreen {

    private ClickableWidget resetCacheButton;

    public MoreCullingClothConfigScreen(Screen parent, Text title, Map<String,
                                        ConfigCategory> categoryMap, Identifier backgroundLocation) {
        super(parent, title, categoryMap, backgroundLocation);
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidths = Math.min(200, (this.width - 50 - 12) / 4);
        this.addDrawableChild(this.resetCacheButton = new ButtonWidget(
                10,
                5,
                buttonWidths,
                20,
                Text.translatable("moreculling.config.resetCache"),
                (widget) -> {
                    CacheUtils.resetAllCache();
                    this.resetCacheButton.active = false;
                })
        );
    }
}
