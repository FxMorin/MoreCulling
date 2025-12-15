package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndGatewayBlock.class)
public abstract class EndGatewayBlock_cullMixin extends BlockWithEntity implements MoreBlockCulling {

    protected EndGatewayBlock_cullMixin(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return MoreCulling.CONFIG.endGatewayCulling ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state) {
        return MoreCulling.CONFIG.endGatewayCulling;
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state, Direction side) {
        return MoreCulling.CONFIG.endGatewayCulling;
    }

    @Override
    public boolean shouldAttemptToCullAgainst(BlockState state, Direction direction) {
        return MoreCulling.CONFIG.endGatewayCulling;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }
}
