package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.cloth.DynamicBooleanBuilder;
import ca.fxco.moreculling.config.cloth.DynamicBooleanListEntry;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuConfig implements ModMenuApi {

    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable(("moreculling.config.title")));
        builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(MoreCullingConfig.class).save());
        ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable("moreculling.config.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        //BlockStates
        generalCategory.addEntry(new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.blockStateCulling"), MoreCulling.CONFIG.useBlockStateCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useBlockStateCulling = newValue)
                .build());

        // Item Frames
        DynamicBooleanListEntry itemFrameLOD = new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrameLOD"), MoreCulling.CONFIG.useItemFrameLOD)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrameLOD = newValue)
                .build();
        DynamicBooleanListEntry itemFrame3FaceCulling = new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrame3FaceCulling"), MoreCulling.CONFIG.useItemFrame3FaceCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrame3FaceCulling = newValue)
                .build();
        generalCategory.addEntry(new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.customItemFrameRenderer"), MoreCulling.CONFIG.useCustomItemFrameRenderer)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useCustomItemFrameRenderer = newValue)
                .setChangeConsumer(newValue -> { // Dynamic ;)
                    itemFrameLOD.setButtonState(newValue);
                    itemFrame3FaceCulling.setButtonState(newValue);
                })
                .build());
        generalCategory.addEntry(itemFrameLOD);
        generalCategory.addEntry(itemFrame3FaceCulling);

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuConfig::createConfigScreen;
    }
}
