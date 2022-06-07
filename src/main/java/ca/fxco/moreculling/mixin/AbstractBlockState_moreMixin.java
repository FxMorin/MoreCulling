package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.patches.MoreStateCulling;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_moreMixin implements MoreStateCulling {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public boolean isSideInvisibleAtPos(BlockState state, Direction direction, BlockPos pos) {
        return ((MoreBlockCulling)this.getBlock()).isSideInvisibleAtPos(this.asBlockState(), state, direction, pos);
    }
}
