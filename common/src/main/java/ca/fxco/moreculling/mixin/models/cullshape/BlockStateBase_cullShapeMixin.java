package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.Util;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static ca.fxco.moreculling.utils.DirectionUtils.*;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBase_cullShapeMixin implements StateCullingShapeCache {

    @Shadow @Final private boolean canOcclude;

    @Shadow protected abstract BlockState asState();

    @Unique
    private static final VoxelShape[] moreculling$EMPTY_OCCLUSION_SHAPES = Util.make(new VoxelShape[DIRECTIONS.length],
            (p_362173_) -> Arrays.fill(p_362173_, Shapes.empty()));;
    @Unique
    private static final VoxelShape[] moreculling$FULL_BLOCK_OCCLUSION_SHAPES = Util.make(
            new VoxelShape[DIRECTIONS.length], (p_361879_) -> Arrays.fill(p_361879_, Shapes.block()));

    @Shadow public abstract Block getBlock();

    @Unique
    private VoxelShape[] moreculling$cullingShapesByFace;

    @Override
    public void moreculling$initShapeCache() {
        VoxelShape voxelShape = null;
        if (!this.canOcclude) {
            if (blockRenderManager != null) {
                BakedModel model = blockRenderManager.getBlockModel(this.asState());
                if (model != null && !this.asState().hasProperty(BlockStateProperties.FACING)) {
                    voxelShape = ((BakedOpacity) model).moreculling$getCullingShape(this.asState());
                }
            }
        }

        if (voxelShape == null) {
            voxelShape = this.getBlock().getOcclusionShape(this.asState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        }

        if (voxelShape == Shapes.empty() || voxelShape.isEmpty()) {
            this.moreculling$cullingShapesByFace = moreculling$EMPTY_OCCLUSION_SHAPES;
        } else if (Block.isShapeFullBlock(voxelShape)) {
            this.moreculling$cullingShapesByFace = moreculling$FULL_BLOCK_OCCLUSION_SHAPES;
        } else {
            this.moreculling$cullingShapesByFace = new VoxelShape[DIRECTIONS.length];

            for (Direction direction : DIRECTIONS) {
                this.moreculling$cullingShapesByFace[direction.ordinal()] = voxelShape.getFaceShape(direction);
            }
        }
    }

    @Override
    public VoxelShape moreculling$getFaceCullingShape(Direction face) {
        return this.moreculling$cullingShapesByFace[face.ordinal()];
    }
}
