package ca.fxco.moreculling.config;

import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.config.sodium.FloatSliderControl;
import ca.fxco.moreculling.config.sodium.IntSliderControl;
import ca.fxco.moreculling.config.sodium.MoreCullingOptionImpl;
import ca.fxco.moreculling.config.sodium.MoreCullingOptionsStorage;
import ca.fxco.moreculling.utils.CompatUtils;
import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SodiumOptionPage {

    /*
        TODO:
        - Add support to custom sodium builder for mod incompatibility so its not jankly done in here
     */

    private static final MoreCullingOptionsStorage morecullingOpts = new MoreCullingOptionsStorage();

    public static OptionPage moreCullingPage() {
        List<OptionGroup> groups = new ArrayList<>();

        // Leaves Culling
        MoreCullingOptionImpl<MoreCullingConfig, Integer> leavesCullingDepth = MoreCullingOptionImpl.createBuilder(int.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.leavesCullingDepth"))
                .setTooltip(CompatUtils.IS_CULLLESSLEAVES_LOADED ?
                        Text.of(Text.translatable("moreculling.config.optionDisabled").getString().formatted("cull-less-leaves")) :
                        Text.translatable("moreculling.config.option.leavesCullingDepth.tooltip"))
                .setControl(option -> new IntSliderControl(option, 1, 4, 1, Text.literal("%d")))
                .setEnabled(!CompatUtils.IS_CULLLESSLEAVES_LOADED && morecullingOpts.getData().leavesCullingMode == LeavesCullingMode.DEPTH)
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .setBinding((opts, value) -> opts.leavesCullingDepth = value, opts -> opts.leavesCullingDepth)
                .build();
        MoreCullingOptionImpl<MoreCullingConfig, LeavesCullingMode> leavesCullingMode = MoreCullingOptionImpl.createBuilder(LeavesCullingMode.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.leavesCulling"))
                .setTooltip(CompatUtils.IS_CULLLESSLEAVES_LOADED ?
                        Text.of(Text.translatable("moreculling.config.optionDisabled").getString().formatted("cull-less-leaves")) :
                        Text.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setControl(option -> new CyclingControl<>(option, LeavesCullingMode.class, LeavesCullingMode.getLocalizedNames()))
                .setBinding((opts, value) -> opts.leavesCullingMode = value, opts -> opts.leavesCullingMode)
                .setEnabled(!CompatUtils.IS_CULLLESSLEAVES_LOADED)
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();
        leavesCullingMode.setEnabledChanged(active -> {
            if (!CompatUtils.IS_CULLLESSLEAVES_LOADED)
                leavesCullingDepth.setAvailable(active && leavesCullingMode.getValue() == LeavesCullingMode.DEPTH);
        });

        // BlockStates
        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setName(Text.translatable("moreculling.config.option.blockStateCulling"))
                        .setTooltip(Text.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.HIGH)
                        .setBinding((opts, value) -> opts.useBlockStateCulling = value, opts -> opts.useBlockStateCulling)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .onEnabledChanged(value -> {
                            if (!CompatUtils.IS_CULLLESSLEAVES_LOADED) leavesCullingMode.setAvailable(value);
                        })
                        .build())
                .build()
        );

        // Item Frames
        MoreCullingOptionImpl<MoreCullingConfig, Integer> itemFrameLODRange = MoreCullingOptionImpl.createBuilder(int.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrameLODRange"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setControl(option -> new IntSliderControl(option, 48, 768, 1, Text.literal("%d")))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrameLOD)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrameLODRange = value, opts -> opts.itemFrameLODRange)
                .build();
        MoreCullingOptionImpl<MoreCullingConfig, Boolean> itemFrameLODOption = MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrameLOD"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrameLOD = value, opts -> opts.useItemFrameLOD)
                .onEnabledChanged(itemFrameLODRange::setAvailable) // Dynamic ;)
                .build();
        MoreCullingOptionImpl<MoreCullingConfig, Float> itemFrame3FaceRange = MoreCullingOptionImpl.createBuilder(float.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setControl(option -> new FloatSliderControl(option, 0.0F, 48.0F, 0.5F, Text.literal("%2.2f")))
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer && morecullingOpts.getData().useItemFrame3FaceCulling)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.itemFrame3FaceCullingRange = value, opts -> opts.itemFrame3FaceCullingRange)
                .build();
        MoreCullingOptionImpl<MoreCullingConfig, Boolean> itemFrame3FaceOption = MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setName(Text.translatable("moreculling.config.option.itemFrame3FaceCulling"))
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrame3FaceCulling = value, opts -> opts.useItemFrame3FaceCulling)
                .onEnabledChanged(itemFrame3FaceRange::setAvailable) // Dynamic ;)
                .build();
        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setName(Text.translatable("moreculling.config.option.customItemFrameRenderer"))
                        .setTooltip(Text.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.HIGH)
                        .setBinding((opts, value) -> opts.useCustomItemFrameRenderer = value, opts -> opts.useCustomItemFrameRenderer)
                        .onEnabledChanged((value) -> { // Dynamic ;)
                            itemFrameLODOption.setAvailable(value);
                            itemFrame3FaceOption.setAvailable(value);
                        })
                        .build())
                .add(itemFrameLODOption)
                .add(itemFrameLODRange)
                .add(itemFrame3FaceOption)
                .add(itemFrame3FaceRange)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(leavesCullingMode)
                .add(leavesCullingDepth)
                .build()
        );

        return new OptionPage(Text.translatable("moreculling.title"), ImmutableList.copyOf(groups));
    }
}
