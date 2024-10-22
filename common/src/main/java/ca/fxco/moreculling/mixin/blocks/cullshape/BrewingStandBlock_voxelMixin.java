package ca.fxco.moreculling.mixin.blocks.cullshape;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BrewingStandBlock.class)
public abstract class BrewingStandBlock_voxelMixin extends BaseEntityBlock implements MoreBlockCulling {
    @Unique
    private static final VoxelShape moreculling$occlusionShape = Shapes.or(
            Block.box(1, 0, 1, 7, 2, 7),
            Block.box(1, 0, 9, 7, 2, 15),
            Block.box(9, 0, 5, 15, 2, 11)
    ) ;

    protected BrewingStandBlock_voxelMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return moreculling$occlusionShape;
    }
}
