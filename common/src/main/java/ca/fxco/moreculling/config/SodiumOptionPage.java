package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.config.*;
import ca.fxco.moreculling.api.config.defaults.ConfigBooleanOption;
import ca.fxco.moreculling.api.config.defaults.ConfigEnumOption;
import ca.fxco.moreculling.api.config.defaults.ConfigFloatOption;
import ca.fxco.moreculling.api.config.defaults.ConfigIntOption;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.config.sodium.MoreCullingSodiumOptionImpl;
import ca.fxco.moreculling.config.sodium.MoreCullingSodiumOptionsStorage;
import ca.fxco.moreculling.platform.Services;
import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.OptionFlag;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.minecraft.network.chat.Component;

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
                .setNameTranslation("moreculling.config.option.cloudCulling")
                .setTooltip(Component.translatable("moreculling.config.option.cloudCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.LOW)
                .setBinding((opts, value) -> opts.cloudCulling = value, opts -> opts.cloudCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // Sign Text Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> signTextCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.signTextCulling")
                .setTooltip(Component.translatable("moreculling.config.option.signTextCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.signTextCulling = value, opts -> opts.signTextCulling)
                .build();

        // Rain/Snow Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> rainCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.rainCulling")
                .setTooltip(Component.translatable("moreculling.config.option.rainCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.rainCulling = value, opts -> opts.rainCulling)
                .build();

        // Beacon Beam Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> beaconBeamCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.beaconBeamCulling")
                .setTooltip(Component.translatable("moreculling.config.option.beaconBeamCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.beaconBeamCulling = value, opts -> opts.beaconBeamCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // Leaves Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Integer> leavesCullingAmount = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.leavesCullingAmount")
                .setTooltip(Component.translatable("moreculling.config.option.leavesCullingAmount.tooltip"))
                .setControl(option -> new SliderControl(option, 1, 4, 1, ControlValueFormatter.number()))
                .setEnabled(morecullingOpts.getData().leavesCullingMode.hasAmount())
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .setBinding((opts, value) -> opts.leavesCullingAmount = value, opts -> opts.leavesCullingAmount)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, LeavesCullingMode> leavesCullingMode = MoreCullingSodiumOptionImpl.createBuilder(LeavesCullingMode.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.leavesCulling")
                .setTooltip(Component.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setControl(option -> new CyclingControl<>(option, LeavesCullingMode.class, LeavesCullingMode.getLocalizedNames()))
                .setBinding((opts, value) -> opts.leavesCullingMode = value, opts -> opts.leavesCullingMode)
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .onChanged((instance, value) -> {
                    leavesCullingAmount.setAvailable(instance.isAvailable() && value.hasAmount());
                    if (MoreCulling.CONFIG.includeMangroveRoots && value == LeavesCullingMode.STATE) {
                        instance.setValue(LeavesCullingMode.CHECK);
                    }
                })
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> includeMangroveRoots = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.includeMangroveRoots")
                .setTooltip(Component.translatable("moreculling.config.option.includeMangroveRoots.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useBlockStateCulling)
                .setImpact(OptionImpact.LOW)
                .onChanged((instance, value) -> {
                    if (value && leavesCullingMode.getValue() == LeavesCullingMode.STATE) {
                        leavesCullingMode.setValue(LeavesCullingMode.CHECK);
                    }
                })
                .setBinding((opts, value) -> opts.includeMangroveRoots = value, opts -> opts.includeMangroveRoots)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // End Gateway Culling
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> endGatewayCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.endGatewayCulling")
                .setTooltip(Component.translatable("moreculling.config.option.endGatewayCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useBlockStateCulling)
                .setImpact(OptionImpact.LOW)
                .setBinding((opts, value) -> opts.endGatewayCulling = value, opts -> opts.endGatewayCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();

        // BlockStates
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> blockStateCulling = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.blockStateCulling")
                .setTooltip(Component.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.useBlockStateCulling = value, opts -> opts.useBlockStateCulling)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .onChanged((instance, value) -> {
                    leavesCullingMode.setAvailable(value);
                    includeMangroveRoots.setAvailable(value);
                    endGatewayCulling.setAvailable(value);
                })
                .build();

        // Item Frames
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrameMapCullingOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.itemFrameMapCulling")
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameMapCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().itemFrameMapCulling)
                .setImpact(OptionImpact.HIGH)
                .setBinding((opts, value) -> opts.itemFrameMapCulling = value, opts -> opts.itemFrameMapCulling)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Integer> itemFrameLODRange = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.itemFrameLODRange")
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setControl(option -> new SliderControl(option, 16, 256, 1, ControlValueFormatter.number()))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrameLOD)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrameLODRange = value, opts -> opts.itemFrameLODRange)
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrameLODOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.itemFrameLOD")
                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrameLOD = value, opts -> opts.useItemFrameLOD)
                .onChanged((instance, value) -> itemFrameLODRange.setAvailable(instance.isAvailable() && value))
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Integer> itemFrame3FaceRange = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.itemFrame3FaceCullingRange")
                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setControl(option -> new SliderControl(option, 2, 16, 1, ControlValueFormatter.number()))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrame3FaceCulling)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrame3FaceCullingRange = value, opts -> Math.round(opts.itemFrame3FaceCullingRange))
                .build();
        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> itemFrame3FaceOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.itemFrame3FaceCulling")
                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrame3FaceCulling = value, opts -> opts.useItemFrame3FaceCulling)
                .onChanged((instance, value) -> itemFrame3FaceRange.setAvailable(instance.isAvailable() && value))
                .build();

        MoreCullingSodiumOptionImpl<MoreCullingConfig, Boolean> paintingBatchingOption = MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setNameTranslation("moreculling.config.option.paintingBatching")
                .setTooltip(Component.translatable("moreculling.config.option.paintingBatching.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().paintingCulling)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.paintingBatching = value, opts -> opts.paintingBatching)
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(rainCulling)
                .add(cloudCulling)
                .add(signTextCulling)
                .add(beaconBeamCulling)
                .add(blockStateCulling)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setNameTranslation("moreculling.config.option.customItemFrameRenderer")
                        .setTooltip(Component.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
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
                .add(MoreCullingSodiumOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setNameTranslation("moreculling.config.option.paintingCulling")
                        .setTooltip(Component.translatable("moreculling.config.option.paintingCulling.tooltip"))
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.MEDIUM)
                        .setBinding((opts, value) -> opts.paintingCulling = value, opts -> opts.paintingCulling)
                        .onChanged((instance, value) -> {
                            paintingBatchingOption.setAvailable(value);
                        })
                        .build())
                .add(paintingBatchingOption)
                .build());

        groups.add(OptionGroup.createBuilder()
                .add(leavesCullingMode)
                .add(leavesCullingAmount)
                .add(includeMangroveRoots)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(endGatewayCulling)
                .build()
        );

        // Generates all groups created through the API
        groups.addAll(generateOptionGroups());

        return new OptionPage(Component.translatable("moreculling.title"), ImmutableList.copyOf(groups));
    }

    public static OptionImpact optionImpactBridge(ConfigOptionImpact optionImpact) {
        if (optionImpact == null) {
            return OptionImpact.LOW;
        }
        return switch (optionImpact) {
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
        return switch (optionFlag) {
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
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                            .setControl(opt -> new SliderControl(opt, floatOption.getMin().intValue(), floatOption.getMax().intValue(), floatOption.getInterval().intValue(), ControlValueFormatter.number()))
                            .setBinding((opts, value) -> option.getSetter().accept(value), opts -> (int) option.getGetter().get());
                } else if (option instanceof ConfigIntOption intOption) {
                    optionBuilder = MoreCullingSodiumOptionImpl.createBuilder(int.class, morecullingOpts)
                            .setControl(opt -> new SliderControl(opt, intOption.getMin(), intOption.getMax(), intOption.getInterval(), ControlValueFormatter.number()))
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
                    optionBuilder.setNameTranslation(option.getTranslationKey())
                            .setTooltip(Component.translatable(option.getTranslationKey() + ".tooltip"))
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
                                Services.PLATFORM.isModLoaded(configModLimit.getLimitedModId()),
                                Component.translatable(configModLimit.getTranslationKey())
                        );
                    }
                    if (option instanceof ConfigModIncompatibility configModIncompatibility) {
                        optionBuilder.setModIncompatibility(
                                Services.PLATFORM.isModLoaded(configModIncompatibility.getIncompatibleModId()),
                                configModIncompatibility.getMessage()
                        );
                    }
                    additionsGroupBuilder.add(optionBuilder.build());
                }
            }
            OptionGroup additionsGroup = additionsGroupBuilder.build();
            if (!additionsGroup.getOptions().isEmpty()) {
                optionGroups.add(additionsGroup); // Sodium group names don't show up
            }
        }
        return optionGroups;
    }
}
