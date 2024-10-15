package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlock_cullMixin extends Block implements MoreBlockCulling {

    public ShulkerBoxBlock_cullMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.block();
    }

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, @Nullable Direction side) {
        return side != state.getValue(DirectionalBlock.FACING).getOpposite();
    }
}
