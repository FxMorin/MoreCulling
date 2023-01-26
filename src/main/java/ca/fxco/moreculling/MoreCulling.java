package ca.fxco.moreculling;

import ca.fxco.moreculling.config.MoreCullingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreCulling implements ClientModInitializer {

    public static BakedModelManager bakedModelManager = null;
    public static BlockRenderManager blockRenderManager = null;

    public static final String MOD_ID = "moreculling";
    public static final TagKey<Block> DONT_CULL = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "dont_cull"));

    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MoreCullingConfig CONFIG;

    @Override
    public void onInitializeClient() {}

    static {
        AutoConfig.register(MoreCullingConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(MoreCullingConfig.class).getConfig();
        MoreCulling.CONFIG.modCompatibility.defaultReturnValue(MoreCulling.CONFIG.useOnModdedBlocksByDefault);
    }
}
