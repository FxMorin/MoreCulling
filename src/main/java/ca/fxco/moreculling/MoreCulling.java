package ca.fxco.moreculling;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.BlockRenderManager;

public class MoreCulling implements ClientModInitializer {

    public static BlockRenderManager blockRenderManager = null;

    //public static final String MOD_ID = "moreculling";
    //public static final TagKey<Block> DONT_CULL = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "dont_cull"));

    /*
        TODO:
        - Make an API so other mods can assign BakedTransparency to there models and further optimize how this works
     */

    @Override
    public void onInitializeClient() {}
}
