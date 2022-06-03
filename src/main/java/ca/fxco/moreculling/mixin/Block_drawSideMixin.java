package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.patches.BakedTransparency;
import ca.fxco.moreculling.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public class Block_drawSideMixin {

    private static BlockRenderManager blockRenderManager = null;

    /**
     * @author Fx Morin
     * @reason Unfortunatly needed for fabric support
     */
    @Overwrite
    public static boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos) {
        if (blockRenderManager == null) blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        return BlockUtils.shouldDrawSideTransparency(blockRenderManager, state, world, pos, side, otherPos, ((BakedTransparency)blockRenderManager.getModel(state)).hasTransparency());
    }
}
