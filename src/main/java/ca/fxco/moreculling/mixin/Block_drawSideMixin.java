package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.patches.BakedTransparency;
import ca.fxco.moreculling.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(Block.class)
public class Block_drawSideMixin {

    /**
     * @author Fx Morin
     * @reason Unfortunately needed for fabric support
     */
    @Overwrite
    public static boolean shouldDrawSide(BlockState state, BlockView world,
                                         BlockPos pos, Direction side, BlockPos otherPos) {
        return BlockUtils.shouldDrawSideTransparency(
                state,
                world,
                pos,
                side,
                otherPos,
                ((BakedTransparency)blockRenderManager.getModel(state)).hasTransparency()
        );
    }
}
