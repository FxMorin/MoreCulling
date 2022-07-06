package ca.fxco.moreculling.config.sodium;

import ca.fxco.moreculling.config.MoreCullingConfig;
import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SodiumOptionPage {

    private static final MoreCullingOptionsStorage morecullingOpts = new MoreCullingOptionsStorage();

    public static OptionPage moreCullingPage() {
        List<OptionGroup> groups = new ArrayList<>();

        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setTranslatableNameId("text.autoconfig.moreculling.option.useBlockStateCulling")
                        .usesAutoConfigTooltip()
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.HIGH)
                        .setBinding((opts, value) -> opts.useBlockStateCulling = value, opts -> opts.useBlockStateCulling)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_UPDATE)
                        .build())
                .build()
        );

        MoreCullingOptionImpl<MoreCullingConfig, Boolean> itemFrameLODOption = MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setTranslatableNameId("text.autoconfig.moreculling.option.useItemFrameLOD")
                .usesAutoConfigTooltip(0)
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrameLOD = value, opts -> opts.useItemFrameLOD)
                .build();
        MoreCullingOptionImpl<MoreCullingConfig, Boolean> itemFrame3FaceOption = MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                .setTranslatableNameId("text.autoconfig.moreculling.option.useItemFrame3FaceCulling")
                .usesAutoConfigTooltip(0)
                .setControl(TickBoxControl::new)
                .setEnabled(morecullingOpts.getData().useCustomItemFrameRenderer)
                .setImpact(OptionImpact.MEDIUM)
                .setBinding((opts, value) -> opts.useItemFrame3FaceCulling = value, opts -> opts.useItemFrame3FaceCulling)
                .build();
        groups.add(OptionGroup.createBuilder()
                .add(MoreCullingOptionImpl.createBuilder(boolean.class, morecullingOpts)
                        .setTranslatableNameId("text.autoconfig.moreculling.option.useCustomItemFrameRenderer")
                        .usesAutoConfigTooltip()
                        .setControl(TickBoxControl::new)
                        .setImpact(OptionImpact.HIGH)
                        .setBinding((opts, value) -> opts.useCustomItemFrameRenderer = value, opts -> opts.useCustomItemFrameRenderer)
                        .setValueModified((value) -> { //Dynamic ;)
                            itemFrameLODOption.setAvailable(value);
                            itemFrame3FaceOption.setAvailable(value);
                        })
                        .build())
                .add(itemFrameLODOption)
                .add(itemFrame3FaceOption)
                .build()
        );

        return new OptionPage(Text.translatable("text.autoconfig.moreculling.title"), ImmutableList.copyOf(groups));
    }
}
