package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndGatewayBlock.class)
public abstract class EndGatewayBlock_cullMixin extends BaseEntityBlock implements MoreBlockCulling {

    protected EndGatewayBlock_cullMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return MoreCulling.CONFIG.endGatewayCulling ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side) {
        return MoreCulling.CONFIG.endGatewayCulling;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.block();
    }
}
