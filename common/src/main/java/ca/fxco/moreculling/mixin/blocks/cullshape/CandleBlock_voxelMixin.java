package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CandleBlock.class)
public abstract class CandleBlock_voxelMixin extends AbstractCandleBlock {
    @Shadow
    @Final
    public static IntegerProperty CANDLES;
    @Unique
    private static final VoxelShape[] moreculling$occlusionShapes = new VoxelShape[]{
            Block.column(2.0, 0.0, 6.0),
            Shapes.or(
                    Block.box(5.0, 0.0, 7.0, 7.0, 5.0, 9.0),
                    Block.box(9.0, 0.0, 6.0, 11.0, 6.0, 8.0)
            ),
            Shapes.or(
                    Block.box(8.0, 0.0, 6.0, 10.0, 6.0, 8.0),
                    Block.box(5.0, 0.0, 7.0, 7.0, 5.0, 9.0),
                    Block.box(7.0, 0.0, 9.0, 9.0, 3.0, 11.0)
            ),
            Shapes.or(
                    Block.box(8.0, 0.0, 5.0, 10.0, 6.0, 7.0),
                    Block.box(5.0, 0.0, 5.0, 7.0, 5.0, 7.0),
                    Block.box(6.0, 0.0, 8.0, 8.0, 3.0, 10.0),
                    Block.box(9.0, 0.0, 8.0, 11.0, 5.0, 10.0)
            )
    };

    protected CandleBlock_voxelMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return moreculling$occlusionShapes[state.getValue(CANDLES) - 1];
    }
}
