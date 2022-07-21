package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.cloth.*;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.MinecraftClient;
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

        // Leaves Culling
        DynamicIntSliderEntry leavesCullingDepth = new DynamicIntSliderBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.leavesCullingDepth"), MoreCulling.CONFIG.leavesCullingDepth - 1, 1, 4)
                .setDefaultValue(2)
                .setTooltip(Text.translatable("moreculling.config.option.leavesCullingDepth.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingDepth = newValue + 1;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .build();
        DynamicEnumEntry<LeavesCullingMode> leavesCullingMode = new DynamicEnumBuilder<>(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.leavesCulling"), LeavesCullingMode.class, MoreCulling.CONFIG.leavesCullingMode)
                .setDefaultValue(LeavesCullingMode.DEFAULT)
                .setTooltip(Text.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingMode = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setChangeConsumer(value -> leavesCullingDepth.setSliderState(value == LeavesCullingMode.DEPTH))
                .build();

        // BlockStates
        generalCategory.addEntry(new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.blockStateCulling"), MoreCulling.CONFIG.useBlockStateCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.useBlockStateCulling = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setChangeConsumer(leavesCullingMode::setButtonState)
                .build());

        // Item Frames
        DynamicIntSliderEntry itemFrameLODRange = new DynamicIntSliderBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrameLODRange"), MoreCulling.CONFIG.itemFrameLODRange, 48, 768) // Between 16 & 256 blocks - 1 & 16 chunks
                .setDefaultValue(384)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrameLODRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrameLOD = new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrameLOD"), MoreCulling.CONFIG.useItemFrameLOD)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrameLOD = newValue)
                .setChangeConsumer(itemFrameLODRange::setSliderState) // Dynamic ;)
                .build();
        DynamicFloatSliderEntry itemFrame3FaceCullingRange = new DynamicFloatSliderBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange"), MoreCulling.CONFIG.itemFrame3FaceCullingRange, 0.0F, 48.0F, 0.5F) // Between 0 & 16 blocks
                .setDefaultValue(12.0F)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrame3FaceCullingRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrame3FaceCulling = new DynamicBooleanBuilder(entryBuilder.getResetButtonKey(), Text.translatable("moreculling.config.option.itemFrame3FaceCulling"), MoreCulling.CONFIG.useItemFrame3FaceCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrame3FaceCulling = newValue)
                .setChangeConsumer(itemFrame3FaceCullingRange::setSliderState) // Dynamic ;)
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
        generalCategory.addEntry(itemFrameLODRange);
        generalCategory.addEntry(itemFrame3FaceCulling);
        generalCategory.addEntry(itemFrame3FaceCullingRange);

        generalCategory.addEntry(leavesCullingMode);
        generalCategory.addEntry(leavesCullingDepth);
        leavesCullingDepth.setSliderState(leavesCullingMode.isEnabled() && MoreCulling.CONFIG.leavesCullingMode == LeavesCullingMode.DEPTH);
        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuConfig::createConfigScreen;
    }
}
