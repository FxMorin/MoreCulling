package ca.fxco.moreculling;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MoreCulling implements ClientModInitializer {

    public static BlockRenderManager blockRenderManager = null;

    public static final String MOD_ID = "moreculling";
    public static final TagKey<Block> DONT_CULL = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "dont_cull"));

    @Override
    public void onInitializeClient() {}
}
