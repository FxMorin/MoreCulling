package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.config.*;
import ca.fxco.moreculling.api.config.defaults.*;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.config.sodium.*;
import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SodiumOptionPage {

    //TODO: Convert all settings to ConfigOption using the MoreCulling config API if those settings can be converted

    private static final MoreCullingSodiumOptionsStorage morecullingOpts = new MoreCullingSodiumOptionsStorage();

    public static OptionPage moreCullingPage() {
        List<OptionGroup> groups = new ArrayList<>();

        // Cloud Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> cloudCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.cloudCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.cloudCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.LOW)
                .setBinding((opts, value) -> opts.cloudCulling = value, opts -> opts.cloudCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        //Sign Text Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> signTextCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.signTextCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.signTextCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.signTextCulling = value, opts -> opts.signTextCulling)
                .build();

        // Entity Model Culling - Does not work with sodium (sodium is faster)
        /*MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> entityModelCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.entityModelCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.entityModelCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.VARIES)
                .setBinding((opts, value) -> opts.entityModelCulling = value, opts -> opts.entityModelCulling)
                .build();*/

        // Leaves Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Integer> leavesCullingAmount = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.leavesCullingAmount"))
                .setTooltip(Text.translatable("moreculling.config.option.leavesCullingAmount.tooltip"))
                .setControl(option -> new IntSliderControl(option, 1, 4, 1, Text.literal("%d")))
                .setEnabled(morecullingOpts.getData().leavesCullingMode.hasAmount())
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .setBinding((opts, value) -> opts.leavesCullingAmount = value, opts -> opts.leavesCullingAmount)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, LeavesCullingMode> leavesCullingMode = MoreCullingSodiumOptionImpl.createBuilder(LeavesCullingMode.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.leavesCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setControl(option -> new CyclingControl<>(option, LeavesCullingMode.class, LeavesCullingMode.getLocalizedNames()))
                .setBinding((opts, value) -> opts.leavesCullingMode = value, opts -> opts.leavesCullingMode)
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .onChanged((instance, value) -> {
                    leavesCullingAmount.setAvailable(instance.isAvailable() && value.hasAmount());
                    if (MoreCulling.CONFIG.includeMangroveRoots && value == LeavesCullingMode.STATE)
                        instance.setValue(LeavesCullingMode.CHECK);
                })
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> includeMangroveRoots = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.includeMangroveRoots"))
                .setTooltip(Text.translatable("moreculling.config.option.includeMangroveRoots.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useBlockStateCulling)
                .setImpact(OptionImpact.LOW)
                .onChanged((instance, value) -> {
                    if (value && leavesCullingMode.getValue() == LeavesCullingMode.STATE)
                        leavesCullingMode.setValue(LeavesCullingMode.CHECK);
                })
                .setBinding((opts, value) -> opts.includeMangroveRoots = value, opts -> opts.includeMangroveRoots)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // Powder Snow Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> powderSnowCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.powderSnowCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.powderSnowCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useBlockStateCulling)
                .setImpact(OptionImpact.LOW)
                .setBinding((opts, value) -> opts.powderSnowCulling = value, opts -> opts.powderSnowCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // BlockStates
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> blockStateCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.blockStateCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.useBlockStateCulling = value, opts -> opts.useBlockStateCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .onChanged((instance,value) -> {
                    leavesCullingMode.setAvailable(value);
                    includeMangroveRoots.setAvailable(value);
                    powderSnowCulling.setAvailable(value);
                })
                .build();

        // Item Frames
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrameMapCullingOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrameMapCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameMapCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().itemFrameMapCulling)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.itemFrameMapCulling = value, opts -> opts.itemFrameMapCulling)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Integer> itemFrameLODRange = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrameLODRange"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setControl(option -> new IntSliderControl(option, 48, 768, 1, Text.literal("%d")))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrameLOD)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrameLODRange = value, opts -> opts.itemFrameLODRange)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrameLODOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrameLOD"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrameLOD = value, opts -> opts.useItemFrameLOD)
                .onChanged((instance, value) -> itemFrameLODRange.setAvailable(instance.isAvailable() && value))
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Float> itemFrame3FaceRange = MoreCullingSodiumOptionImpl.createBuilder(float.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setControl(option -> new FloatSliderControl(option, 0.0F, 48.0F, 0.5F, Text.literal("%2.2f")))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrame3FaceCulling)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrame3FaceCullingRange = value, opts -> opts.itemFrame3FaceCullingRange)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrame3FaceOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrame3FaceCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrame3FaceCulling = value, opts -> opts.useItemFrame3FaceCulling)
                .onChanged((instance, value) -> itemFrame3FaceRange.setAvailable(instance.isAvailable() && value))
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(cloudCulling)
                .add(signTextCulling)
                .add(blockStateCulling)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setName(Text.translatable("moreculling.config.option.customItemFrameRenderer"))
                        .setTooltip(Text.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.HIGH)
                        .setBinding((opts, value) -> opts.useCustomItemFrameRenderer = value, opts -> opts.useCustomItemFrameRenderer)
                        .onChanged((instance, value) -> {
                            itemFrameLODOption.setAvailable(value);
                            itemFrame3FaceOption.setAvailable(value);
                            itemFrameMapCullingOption.setAvailable(value);
                        })
                        .build())
                .add(itemFrameMapCullingOption)
                .add(itemFrameLODOption)
                .add(itemFrameLODRange)
                .add(itemFrame3FaceOption)
                .add(itemFrame3FaceRange)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(leavesCullingMode)
                .add(leavesCullingAmount)
                .add(includeMangroveRoots)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(powderSnowCulling)
                .build()
        );

        // Generates all groups created through the API
        groups.addAll(generateOptionGroups());

        return new OptionPage(Text.translatable("moreculling.title"), ImmutableList.copyOf(groups));
    }

    public static OptionImpact optionImpactBridge(ConfigOptionImpact optionImpact) {
        if (optionImpact == null) {
            return OptionImpact.LOW;
        }
        return switch(optionImpact) {
            case LOW -> OptionImpact.LOW;
            case MEDIUM -> OptionImpact.MEDIUM;
            case HIGH -> OptionImpact.HIGH;
            case VARIES -> OptionImpact.VARIES;
        };
    }

    public static OptionFlag optionFlagBridge(ConfigOptionFlag optionFlag) {
        if (optionFlag == null) {
            return null;
        }
        return switch(optionFlag) {
            case REQUIRES_RENDERER_RELOAD -> OptionFlag.REQUIRES_RENDERER_RELOAD;
            case REQUIRES_ASSET_RELOAD -> OptionFlag.REQUIRES_ASSET_RELOAD;
            case REQUIRES_GAME_RESTART -> OptionFlag.REQUIRES_GAME_RESTART;
            case REQUIRES_RENDERER_UPDATE -> OptionFlag.REQUIRES_RENDERER_UPDATE;
        };
    }

    //TODO: Add ModMenu option to the Sodium options
    @SuppressWarnings("unchecked")
    public static List<OptionGroup> generateOptionGroups() {
        Map<String, List<ConfigOption<?>>> groupedOptions = ConfigAdditions.getOptions();
        List<OptionGroup> optionGroups = new LinkedList<>();
        for (String group : groupedOptions.keySet()) {
            OptionGroup.Builder additionsGroupBuilder = OptionGroup.createBuilder();
            for (ConfigOption option : groupedOptions.get(group)) {
                MoreCullingSodiumOptionImpl.Builder<?, ?> optionBuilder;
                if (option instanceof ConfigBooleanOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                            .setControl(TickBoxControl::new)
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> (Boolean) option.getGetter().get());
                } else if (option instanceof ConfigFloatOption floatOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(float.class, morecullingOpts)
                            .setControl(opt -> new FloatSliderControl(opt, floatOption.getMin(), floatOption.getMax(), floatOption.getInterval(), Text.literal(floatOption.getStringFormat())))
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> (Float) option.getGetter().get());
                } else if (option instanceof ConfigIntOption intOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                            .setControl(opt -> new IntSliderControl(opt, intOption.getMin(), intOption.getMax(), intOption.getInterval(), Text.literal(intOption.getStringFormat())))
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> (Integer) option.getGetter().get());
                } else if (option instanceof ConfigEnumOption<?> enumOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(Enum.class, morecullingOpts)
                            .setControl(opt -> new CyclingControl(opt, enumOption.getTypeClass(), enumOption.getLocalizedNames()))
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> (Enum<?>) option.getGetter().get());
                } else if (option instanceof ConfigSodiumOption objOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(objOption.getTypeClass(), morecullingOpts)
                            .setControl(opt -> objOption.getControl())
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> option.getGetter().get());
                } else {
                    optionBuilder = null;
                }
                if (optionBuilder != null) {
                    optionBuilder.setName(Text.translatable(option.getTranslationKey()))
                            .setTooltip(Text.translatable(option.getTranslationKey() + ".tooltip"))
                            .setEnabled(option.setEnabled())
                            .onChanged((instance, value) -> {
                                if (option.getChanged() != null) {
                                    option.getChanged().accept(value);
                                }
                            });
                    if (option.getImpact() != null) {
                        optionBuilder.setImpact(optionImpactBridge(option.getImpact()));
                    }
                    if (option.getFlag() != null) {
                        optionBuilder.setFlags(optionFlagBridge(option.getFlag()));
                    }
                    if (option instanceof ConfigModLimit configModLimit) {
                        optionBuilder.setModLimited(
                                FabricLoader.getInstance().isModLoaded(configModLimit.getLimitedModId()),
                                Text.translatable(configModLimit.getTranslationKey())
                        );
                    }
                    if (option instanceof ConfigModIncompatibility configModIncompatibility) {
                        optionBuilder.setModIncompatibility(
                                FabricLoader.getInstance().isModLoaded(configModIncompatibility.getIncompatibleModId()),
                                configModIncompatibility.getMessage()
                        );
                    }
                    additionsGroupBuilder.add(optionBuilder.build());
                }
            }
            OptionGroup additionsGroup = additionsGroupBuilder.build();
            if (additionsGroup.getOptions().size() > 0) {
                optionGroups.add(additionsGroup); // Sodium group names don't show up
            }
        }
        return optionGroups;
    }
}
