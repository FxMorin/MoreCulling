package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LightningRodBlock.class)
public class LightningRodBlock_voxelMixin extends Block {

    @Unique
    private static final VoxelShape[] moreculling$SHAPES = new VoxelShape[] {
        Shapes.or(Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)),
        Shapes.or(Block.box(6.0, 12.0, 6.0, 10.0, 16.0, 10.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)),
        Shapes.or(Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 4.0), Block.box(7.0, 7.0, 0.0, 9.0, 9.0, 16.0)),
        Shapes.or(Block.box(6.0, 6.0, 12.0, 10.0, 10.0, 16.0), Block.box(7.0, 7.0, 0.0, 9.0, 9.0, 16.0)),
        Shapes.or(Block.box(0.0, 6.0, 6.0, 4.0, 10.0, 10.0), Block.box(0.0, 7.0, 7.0, 16.0, 9.0, 9.0)),
        Shapes.or(Block.box(12.0, 6.0, 6.0, 16.0, 10.0, 10.0), Block.box(0.0, 7.0, 7.0, 16.0, 9.0, 9.0))
    };

    public LightningRodBlock_voxelMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return moreculling$SHAPES[state.getValue(BlockStateProperties.FACING).ordinal()];
    }
}
