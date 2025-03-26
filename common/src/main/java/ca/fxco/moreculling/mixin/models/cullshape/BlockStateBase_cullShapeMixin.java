package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
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

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBase_cullShapeMixin implements StateCullingShapeCache {

    @Shadow @Final private boolean canOcclude;

    @Shadow protected abstract BlockState asState();

    @Shadow private VoxelShape[] occlusionShapesByFace;

    @Shadow @Final private static VoxelShape[] EMPTY_OCCLUSION_SHAPES;
    @Shadow @Final private static VoxelShape[] FULL_BLOCK_OCCLUSION_SHAPES;
    @Shadow @Final private static Direction[] DIRECTIONS;

    @Shadow public abstract Block getBlock();

    @Unique
    private VoxelShape[] moreculling$cullingShapesByFace;

    @Inject(
            method = "initCache",
            at = @At(
                    value = "TAIL"
            )
    )
    private void moreculling$customCullingShape(CallbackInfo ci) {
        VoxelShape voxelShape = null;
        if (blockRenderManager != null) {
            BlockStateModel model = blockRenderManager.getBlockModel(this.asState());
            if (model != null) {
                if (((BakedOpacity) model).moreculling$getHasAutoModelShape() && this.canOcclude) {
                    this.moreculling$cullingShapesByFace = occlusionShapesByFace;
                    return;
                }
                if (!this.asState().hasProperty(BlockStateProperties.FACING)) {
                    voxelShape = ((BakedOpacity) model).moreculling$getCullingShape(this.asState());
                }
            }
        }

        if (voxelShape == null) {
            if (this.canOcclude) {
                this.moreculling$cullingShapesByFace = occlusionShapesByFace;
                return;
            }
            voxelShape = this.getBlock().getOcclusionShape(this.asState());
        }

        if (voxelShape == Shapes.empty() || voxelShape.isEmpty()) {
            this.moreculling$cullingShapesByFace = EMPTY_OCCLUSION_SHAPES;
        } else if (Block.isShapeFullBlock(voxelShape)) {
            this.moreculling$cullingShapesByFace = FULL_BLOCK_OCCLUSION_SHAPES;
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
