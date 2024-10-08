package ca.fxco.moreculling;

import ca.fxco.moreculling.config.ConfigUpdater;
import ca.fxco.moreculling.config.MoreCullingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreCulling {

    public static int CURRENT_VERSION = 1;

    public static ModelManager bakedModelManager = null;
    public static BlockRenderDispatcher blockRenderManager = null;

    public static final String MOD_ID = "moreculling";
    //TODO: make so it would work on the servers too
    public static final TagKey<Block> DONT_CULL = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(MOD_ID, "dont_cull"));

    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MoreCullingConfig CONFIG;

    public static void init() {
        AutoConfig.register(MoreCullingConfig.class, (conf, clazz) -> new Toml4jConfigSerializer<>(conf, clazz) {
            public MoreCullingConfig deserialize() {
                try {
                    return super.deserialize();
                } catch (Exception e) {
                    return this.createDefault();
                }
            }
        });
        CONFIG = AutoConfig.getConfigHolder(MoreCullingConfig.class).getConfig();
        ConfigUpdater.updateConfig(CONFIG);
        CONFIG.modCompatibility.defaultReturnValue(CONFIG.useOnModdedBlocksByDefault);
    }

    public void saveConfig() {
        AutoConfig.getConfigHolder(MoreCullingConfig.class).save();
    }
}
