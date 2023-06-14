package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlock_cullShapeMixin extends Block implements MoreBlockCulling {

    public ShulkerBoxBlock_cullShapeMixin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean cantCullAgainst(BlockState state, @Nullable Direction side) {
        return side != state.get(FacingBlock.FACING).getOpposite();
    }
}
