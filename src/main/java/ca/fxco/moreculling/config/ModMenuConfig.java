package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.config.*;
import ca.fxco.moreculling.api.config.defaults.ConfigBooleanOption;
import ca.fxco.moreculling.api.config.defaults.ConfigEnumOption;
import ca.fxco.moreculling.api.config.defaults.ConfigFloatOption;
import ca.fxco.moreculling.api.config.defaults.ConfigIntOption;
import ca.fxco.moreculling.config.cloth.*;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CompatUtils;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModMenuConfig implements ModMenuApi {

    //TODO: Convert all settings to ConfigOption using the MoreCulling config API if those settings can be converted

    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = MoreCullingClothConfigBuilder.create().setParentScreen(parent);
        builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(MoreCullingConfig.class).save());
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("moreculling.config.category.general"));
        ConfigCategory compatCategory = builder.getOrCreateCategory(Component.translatable("moreculling.config.category.compat"));

        // Modded Blocks
        List<DynamicBooleanListEntry> modsOption = new ArrayList<>();
        DynamicBooleanListEntry useOnModdedBlocks = new DynamicBooleanBuilder("moreculling.config.option.useOnModdedBlocks")
                .setValue(MoreCulling.CONFIG.useOnModdedBlocksByDefault)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.useOnModdedBlocks.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useOnModdedBlocksByDefault = newValue)
                .requireRestart() //TODO: Just need to reset the gui
                .build();
        for (Object2BooleanMap.Entry<String> entry : MoreCulling.CONFIG.modCompatibility.object2BooleanEntrySet()) {
            String modId = entry.getKey();
            if (modId.equals("minecraft")) {
                continue;
            }
            ModContainer con = FabricLoader.getInstance().getModContainer(modId).orElse(null);
            DynamicBooleanListEntry aMod = new DynamicBooleanBuilder(con == null ? modId : con.getMetadata().getName())
                    .setValue(entry.getBooleanValue())
                    .setDefaultValue(MoreCulling.CONFIG.useOnModdedBlocksByDefault)
                    .setTooltip(Component.literal(modId))
                    .setSaveConsumer(v -> {
                        MoreCulling.CONFIG.modCompatibility.put(modId, v.booleanValue());
                        BuiltInRegistries.BLOCK.forEach(block -> {
                            if (v != ((MoreBlockCulling) block).moreculling$canCull() && BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(modId)) {
                                ((MoreBlockCulling) block).moreculling$setCanCull(v);
                            }
                        });
                    })
                    .build();
            modsOption.add(aMod);
        }

        // Cloud Culling
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.cloudCulling")
                .setValue(MoreCulling.CONFIG.cloudCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.cloudCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.cloudCulling = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .build());

        // Sign Text Culling
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.signTextCulling")
                .setValue(MoreCulling.CONFIG.signTextCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.signTextCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.signTextCulling = newValue)
                .build());

        // Entity Model Culling
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.entityModelCulling")
                .setValue(MoreCulling.CONFIG.entityModelCulling)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("moreculling.config.option.entityModelCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.entityModelCulling = newValue)
                .setModIncompatibility(CompatUtils.IS_SODIUM_LOADED, "sodium")
                .build());

        // Rain/Snow Culling
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.rainCulling")
                .setValue(MoreCulling.CONFIG.rainCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.rainCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.rainCulling = newValue)
                .build());

        // Beacon Beam Culling
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.beaconBeamCulling")
                .setValue(MoreCulling.CONFIG.beaconBeamCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.beaconBeamCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.beaconBeamCulling = newValue)
                .build());

        // Leaves Culling
        DynamicIntSliderEntry leavesCullingAmount = new DynamicIntSliderBuilder("moreculling.config.option.leavesCullingAmount", 1, 4)
                .setValue(MoreCulling.CONFIG.leavesCullingAmount)
                .setDefaultValue(2)
                .setTooltip(Component.translatable("moreculling.config.option.leavesCullingAmount.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingAmount = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .build();
        DynamicEnumEntry<LeavesCullingMode> leavesCullingMode = new DynamicEnumBuilder<>("moreculling.config.option.leavesCulling", LeavesCullingMode.class)
                .setValue(MoreCulling.CONFIG.leavesCullingMode)
                .setDefaultValue(LeavesCullingMode.DEFAULT)
                .setTooltip(Component.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingMode = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .setChangeConsumer((instance, value) -> {
                    leavesCullingAmount.setEnabledState(instance.isEnabled() && value.hasAmount());
                    if (MoreCulling.CONFIG.includeMangroveRoots && value == LeavesCullingMode.STATE) {
                        instance.setValue(LeavesCullingMode.CHECK);
                    }
                })
                .build();
        DynamicBooleanListEntry includeMangroveRoots = new DynamicBooleanBuilder("moreculling.config.option.includeMangroveRoots")
                .setValue(MoreCulling.CONFIG.includeMangroveRoots)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("moreculling.config.option.includeMangroveRoots.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.includeMangroveRoots = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .setChangeConsumer((instance, value) -> {
                    if (value && leavesCullingMode.getValue() == LeavesCullingMode.STATE) {
                        leavesCullingMode.setValue(LeavesCullingMode.CHECK);
                    }
                })
                .build();

        // Powder Snow Culling
        DynamicBooleanListEntry powderSnowCulling = new DynamicBooleanBuilder("moreculling.config.option.powderSnowCulling")
                .setValue(MoreCulling.CONFIG.powderSnowCulling)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("moreculling.config.option.powderSnowCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.powderSnowCulling = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .build();

        // End Gateway Culling
        DynamicBooleanListEntry endGatewayCulling = new DynamicBooleanBuilder("moreculling.config.option.endGatewayCulling")
                .setValue(MoreCulling.CONFIG.endGatewayCulling)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("moreculling.config.option.endGatewayCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.endGatewayCulling = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .build();

        // BlockStates
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.blockStateCulling")
                .setValue(MoreCulling.CONFIG.useBlockStateCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.useBlockStateCulling = newValue;
                    Minecraft mc = Minecraft.getInstance();
                    if (mc != null && mc.levelRenderer != null) {
                        mc.levelRenderer.allChanged();
                    }
                })
                .setChangeConsumer((instance, value) -> {
                    leavesCullingMode.setEnabledState(value);
                    includeMangroveRoots.setEnabledState(value);
                    powderSnowCulling.setEnabledState(value);
                    useOnModdedBlocks.setEnabledState(value);
                    for (DynamicBooleanListEntry entry : modsOption) {
                        entry.setEnabledState(value);
                    }
                })
                .build());

        // Item Frames
        DynamicBooleanListEntry itemFrameMapCulling = new DynamicBooleanBuilder("moreculling.config.option.itemFrameMapCulling")
                .setValue(MoreCulling.CONFIG.itemFrameMapCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameMapCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrameMapCulling = newValue)
                .build();
        DynamicIntSliderEntry itemFrameLODRange = new DynamicIntSliderBuilder("moreculling.config.option.itemFrameLODRange", 16, 256) // Between 16 & 256 blocks - 1 & 16 chunks
                .setValue(MoreCulling.CONFIG.itemFrameLODRange)
                .setDefaultValue(128)
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrameLODRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrameLOD = new DynamicBooleanBuilder("moreculling.config.option.itemFrameLOD")
                .setValue(MoreCulling.CONFIG.useItemFrameLOD)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrameLOD = newValue)
                .setChangeConsumer((instance, value) -> itemFrameLODRange.setEnabledState(value))
                .build();
        DynamicFloatSliderEntry itemFrame3FaceCullingRange = new DynamicFloatSliderBuilder("moreculling.config.option.itemFrame3FaceCullingRange", 2F, 16F, 0.2F) // Between 0 & 16 blocks
                .setValue(MoreCulling.CONFIG.itemFrame3FaceCullingRange)
                .setDefaultValue(4F)
                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrame3FaceCullingRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrame3FaceCulling = new DynamicBooleanBuilder("moreculling.config.option.itemFrame3FaceCulling")
                .setValue(MoreCulling.CONFIG.useItemFrame3FaceCulling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrame3FaceCulling = newValue)
                .setChangeConsumer((instance, value) -> itemFrame3FaceCullingRange.setEnabledState(value))
                .build();
        generalCategory.addEntry(new DynamicBooleanBuilder("moreculling.config.option.customItemFrameRenderer")
                .setValue(MoreCulling.CONFIG.useCustomItemFrameRenderer)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useCustomItemFrameRenderer = newValue)
                .setChangeConsumer((instance, value) -> {
                    itemFrameLOD.setEnabledState(value);
                    itemFrame3FaceCulling.setEnabledState(value);
                    itemFrameMapCulling.setEnabledState(value);
                })
                .build());
        generalCategory.addEntry(itemFrameMapCulling);
        generalCategory.addEntry(itemFrameLOD);
        generalCategory.addEntry(itemFrameLODRange);
        generalCategory.addEntry(itemFrame3FaceCulling);
        generalCategory.addEntry(itemFrame3FaceCullingRange);

        generalCategory.addEntry(leavesCullingMode);
        generalCategory.addEntry(leavesCullingAmount);
        generalCategory.addEntry(includeMangroveRoots);

        generalCategory.addEntry(powderSnowCulling);
        generalCategory.addEntry(endGatewayCulling);
        leavesCullingAmount.setEnabledState(leavesCullingMode.isEnabled() && MoreCulling.CONFIG.leavesCullingMode == LeavesCullingMode.DEPTH);

        compatCategory.addEntry(useOnModdedBlocks);
        for (DynamicBooleanListEntry entry : modsOption) {
            compatCategory.addEntry(entry);
        }

        // Generates all categories created through the API
        generateConfigCategories(builder, generalCategory);

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuConfig::createConfigScreen;
    }

    //TODO: Add more Sodium option to the ModMenu options
    @SuppressWarnings("unchecked")
    public static void generateConfigCategories(ConfigBuilder builder, ConfigCategory generalCategory) {
        Map<String, List<ConfigOption<?>>> groupedOptions = ConfigAdditions.getOptions();
        for (String group : groupedOptions.keySet()) {
            ConfigCategory category;
            if (ConfigAdditions.isGroupSeparate(group)) {
                category = builder.getOrCreateCategory(Component.translatable("moreculling.config.category." + group));
            } else {
                category = generalCategory;
            }
            for (ConfigOption option : groupedOptions.get(group)) {
                AbstractDynamicBuilder optionBuilder;
                if (option instanceof ConfigBooleanOption) {
                    optionBuilder = new DynamicBooleanBuilder(option.getTranslationKey());
                } else if (option instanceof ConfigFloatOption floatOption) {
                    optionBuilder = new DynamicFloatSliderBuilder(option.getTranslationKey(), floatOption.getMin(), floatOption.getMax(), floatOption.getInterval());
                } else if (option instanceof ConfigIntOption intOption) {
                    optionBuilder = new DynamicIntSliderBuilder(option.getTranslationKey(), intOption.getMin(), intOption.getMax());
                } else if (option instanceof ConfigEnumOption<?> enumOption) {
                    optionBuilder = new DynamicEnumBuilder<>(option.getTranslationKey(), enumOption.getTypeClass());
                } else {
                    optionBuilder = null;
                }
                if (optionBuilder != null) {
                    optionBuilder.setTooltip(Component.translatable(option.getTranslationKey() + ".tooltip"))
                            .setValue(option.getGetter().get())
                            .setDefaultValue(option.getDefaultValue())
                            .setSaveConsumer(newValue -> option.getSetter().accept(newValue))
                            .setChangeConsumer((instance, value) -> {
                                if (option.getChanged() != null) {
                                    option.getChanged().accept(value);
                                }
                            });
                    if (option instanceof ConfigModLimit configModLimit) {
                        optionBuilder.setModLimited(
                                FabricLoader.getInstance().isModLoaded(configModLimit.getLimitedModId()),
                                Component.translatable(configModLimit.getTranslationKey())
                        );
                    }
                    if (option instanceof ConfigModIncompatibility configModIncompatibility) {
                        optionBuilder.setModIncompatibility(
                                FabricLoader.getInstance().isModLoaded(configModIncompatibility.getIncompatibleModId()),
                                configModIncompatibility.getMessage()
                        );
                    }
                    if (option.getFlag() == ConfigOptionFlag.REQUIRES_GAME_RESTART) {
                        optionBuilder.requireRestart();
                    }
                    category.addEntry(optionBuilder.build());
                }
            }
        }
    }
}
