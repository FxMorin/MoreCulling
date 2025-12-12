package ca.fxco.moreculling.config.sodium;

import ca.fxco.moreculling.config.MoreCullingConfig;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MoreCullingSodiumConfigBuilder implements ConfigEntryPoint {
    private final MoreCullingSodiumOptionsStorage storage = new MoreCullingSodiumOptionsStorage();
    private final StorageEventHandler handler = this.storage::save; // typically gets referenced many times
    private static final Identifier LEAVES_CULLING = Identifier.parse("moreculling:leaves_culling");
    private static final Identifier BLOCK_STATE = Identifier.parse("moreculling:block_state_culling");
    private static final Identifier ITEM_RENDERER = Identifier.parse("moreculling:custom_item_frame_renderer");
    private static final Identifier ITEM_3F_CULLING = Identifier.parse("moreculling:item_frame_3_face_culling");
    private static final Identifier ITEM_LOD = Identifier.parse("moreculling:item_frame_lod");

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .setIcon(Identifier.parse("moreculling:more_culling.png"))
                .addPage(builder.createOptionPage()
                        .setName(Component.translatable("moreculling.config.category.general"))
                        .addOptionGroup(builder.createOptionGroup()
                                // Rain/Snow Culling
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:rain_culling"))
                                                .setName(Component.translatable("moreculling.config.option.rainCulling")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.rainCulling.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setStorageHandler(this.handler)
                                                .setBinding(this.storage::setRainCulling, this.storage::getRainCulling)
                                                .setDefaultValue(true)
                                )
                                // Cloud Culling
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:cloud_culling"))
                                        .setName(Component.translatable("moreculling.config.option.cloudCulling")) // use translation keys here
                                        .setTooltip(Component.translatable("moreculling.config.option.cloudCulling.tooltip"))
                                        .setImpact(OptionImpact.LOW)
                                        .setStorageHandler(this.handler)
                                        .setBinding(this.storage::setCloudCulling, this.storage::getCloudCulling)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .setDefaultValue(true)
                                )
                                // Sign Text Culling
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:sign_text_culling"))
                                        .setName(Component.translatable("moreculling.config.option.signTextCulling")) // use translation keys here
                                        .setTooltip(Component.translatable("moreculling.config.option.signTextCulling.tooltip"))
                                        .setImpact(OptionImpact.HIGH)
                                        .setStorageHandler(this.handler)
                                        .setBinding(this.storage::setSignTextCulling, this.storage::getSignTextCulling)
                                                .setDefaultValue(true)
                                )
                                // Beacon Beam Culling
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:beacon_beam_culling"))
                                        .setName(Component.translatable("moreculling.config.option.beaconBeamCulling")) // use translation keys here
                                        .setTooltip(Component.translatable("moreculling.config.option.beaconBeamCulling.tooltip"))
                                        .setImpact(OptionImpact.MEDIUM)
                                        .setStorageHandler(this.handler)
                                        .setBinding(this.storage::setBeaconBeamCulling, this.storage::getBeaconBeamCulling)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                                .setDefaultValue(true)
                                )
                                // BlockStates
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:block_state_culling"))
                                                .setName(Component.translatable("moreculling.config.option.blockStateCulling")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                                                .setImpact(OptionImpact.HIGH)
                                                .setStorageHandler(this.handler)
                                                .setBinding(this.storage::setUseBlockStateCulling, this.storage::getUseBlockStateCulling)
                                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                                .setDefaultValue(true)
                                )
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:end_gateway_culling"))
                                                .setName(Component.translatable("moreculling.config.option.endGatewayCulling")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.endGatewayCulling.tooltip"))
                                                .setImpact(OptionImpact.LOW)
                                                .setStorageHandler(this.handler)
                                                .setBinding(this.storage::setEndGatewayCulling, this.storage::getEndGatewayCulling)
                                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                                .setEnabled(storage.getData().useBlockStateCulling)
                                                .setDefaultValue(false)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(BLOCK_STATE), BLOCK_STATE)
                                )
                        )
                ).addPage(builder.createOptionPage()
                        .setName(Component.translatable("moreculling.config.category.item_frame"))
                        .addOptionGroup(builder.createOptionGroup()
                                // Item Frames
                                .addOption(
                                        builder.createBooleanOption(ITEM_RENDERER)
                                                .setName(Component.translatable("moreculling.config.option.customItemFrameRenderer")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                                                .setImpact(OptionImpact.HIGH)
                                                .setStorageHandler(this.handler)
                                                .setBinding(value -> this.storage.getData().useCustomItemFrameRenderer = value, () -> this.storage.getData().useCustomItemFrameRenderer)
                                                .setDefaultValue(true)
                                )
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:item_frame_map_culling"))
                                                .setName(Component.translatable("moreculling.config.option.itemFrameMapCulling")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.itemFrameMapCulling.tooltip"))
                                                .setImpact(OptionImpact.HIGH)
                                                .setStorageHandler(this.handler)
                                                .setBinding(value -> this.storage.getData().itemFrameMapCulling = value, () -> this.storage.getData().itemFrameMapCulling)
                                                .setDefaultValue(true)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(ITEM_RENDERER), ITEM_RENDERER)
                                )
                                .addOption(
                                        builder.createBooleanOption(ITEM_LOD)
                                                .setName(Component.translatable("moreculling.config.option.itemFrameLOD")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setStorageHandler(this.handler)
                                                .setBinding(value -> this.storage.getData().useItemFrameLOD = value, () -> this.storage.getData().useItemFrameLOD)
                                                .setDefaultValue(true)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(ITEM_RENDERER), ITEM_RENDERER)
                                )
                                .addOption(
                                        builder.createIntegerOption(Identifier.parse("moreculling:item_frame_lodrange"))
                                                .setName(Component.translatable("moreculling.config.option.itemFrameLODRange")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setRange(16, 256, 1)
                                                .setValueFormatter(ControlValueFormatterImpls.number())
                                                .setStorageHandler(this.handler)
                                                .setBinding(value -> this.storage.getData().itemFrameLODRange = value, () -> this.storage.getData().itemFrameLODRange)
                                                .setDefaultValue(128)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(ITEM_RENDERER) && configState.readBooleanOption(ITEM_LOD), ITEM_RENDERER, ITEM_LOD)
                                )
                                .addOption(
                                        builder.createBooleanOption(ITEM_3F_CULLING)
                                                .setName(Component.translatable("moreculling.config.option.itemFrame3FaceCulling"))
                                                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setStorageHandler(this.handler)
                                                .setEnabled(storage.getData().useCustomItemFrameRenderer)
                                                .setBinding(value -> this.storage.getData().useItemFrame3FaceCulling = value, () -> this.storage.getData().useItemFrame3FaceCulling)
                                                .setDefaultValue(true)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(ITEM_RENDERER), ITEM_RENDERER)
                                )
                                .addOption(
                                        builder.createIntegerOption(Identifier.parse("moreculling:item_frame_3_face_culling_range"))
                                                .setName(Component.translatable("moreculling.config.option.itemFrame3FaceCullingRange"))
                                                .setTooltip(Component.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setRange(2, 16, 1)
                                                .setValueFormatter(ControlValueFormatterImpls.number())
                                                .setStorageHandler(this.handler)
                                                .setBinding(value -> this.storage.getData().itemFrame3FaceCullingRange = value, () -> (int) this.storage.getData().itemFrame3FaceCullingRange)
                                                .setDefaultValue(4)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(ITEM_RENDERER) && configState.readBooleanOption(ITEM_3F_CULLING), ITEM_RENDERER, ITEM_3F_CULLING)
                                )
                        )
                ).addPage(builder.createOptionPage()
                        .setName(Component.translatable("moreculling.config.category.leaves"))
                        .addOptionGroup(builder.createOptionGroup()
                                // Leaves Culling
                                .addOption(
                                        builder.createEnumOption(LEAVES_CULLING, LeavesCullingMode.class)
                                                .setName(Component.translatable("moreculling.config.option.leavesCulling")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.leavesCulling.tooltip"))
                                                .setImpact(OptionImpact.MEDIUM)
                                                .setStorageHandler(this.handler)
                                                .setBinding(this.storage::setLeavesCullingMode, this.storage::getLeavesCullingMode)
                                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                                .setDefaultValue(LeavesCullingMode.DEFAULT).setElementNameProvider(LeavesCullingMode::getText)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(BLOCK_STATE), BLOCK_STATE)
                                )
                                .addOption(
                                        builder.createIntegerOption(Identifier.parse("moreculling:leaves_culling_amount"))
                                        .setName(Component.translatable("moreculling.config.option.leavesCullingAmount")) // use translation keys here
                                        .setTooltip(Component.translatable("moreculling.config.option.leavesCullingAmount.tooltip"))
                                        .setImpact(OptionImpact.MEDIUM)
                                        .setStorageHandler(this.handler)
                                        .setBinding(this.storage::setLeavesCullingAmount, this.storage::getLeavesCullingAmount)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .setRange(1, 4, 1)
                                        .setValueFormatter(ControlValueFormatterImpls.number())
                                        .setDefaultValue(2)
                                        .setEnabledProvider(configState ->
                                                configState.readEnumOption(LEAVES_CULLING, LeavesCullingMode.class).hasAmount()
                                                        && configState.readBooleanOption(BLOCK_STATE), LEAVES_CULLING, BLOCK_STATE)

                                )
                                .addOption(
                                        builder.createBooleanOption(Identifier.parse("moreculling:include_mangrove_roots"))
                                                .setName(Component.translatable("moreculling.config.option.includeMangroveRoots")) // use translation keys here
                                                .setTooltip(Component.translatable("moreculling.config.option.includeMangroveRoots.tooltip"))
                                                .setImpact(OptionImpact.LOW)
                                                .setStorageHandler(this.handler)
                                                .setBinding(this.storage::setIncludeMangroveRoots, this.storage::getIncludeMangroveRoots)
                                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                                .setEnabled(storage.getData().useBlockStateCulling)
                                                .setDefaultValue(false)
                                                .setEnabledProvider(configState -> configState.readBooleanOption(BLOCK_STATE), BLOCK_STATE)
                                )
                        )
                );
    }
}
