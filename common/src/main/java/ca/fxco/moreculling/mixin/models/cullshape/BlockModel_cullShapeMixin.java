package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import ca.fxco.moreculling.utils.ShapeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(BlockModel.class)
public abstract class BlockModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Shadow
    @Nullable
    protected BlockModel parent;

    @Shadow
    public abstract List<BlockElement> getElements();

    @Unique
    @Nullable
    private List<CullShapeElement> moreculling$cullShapeElements = null;

    @Unique
    private boolean moreculling$useModelShape = false;

    @Override
    public void moreculling$setCullShapeElements(@Nullable List<CullShapeElement> cullShapeElements) {
        this.moreculling$cullShapeElements = cullShapeElements;
    }

    @Override
    public @Nullable List<CullShapeElement> moreculling$getCullShapeElements(ResourceLocation id) {
        if (this.moreculling$cullShapeElements == null) {
            return this.parent != null ?
                    ((ExtendedUnbakedModel) this.parent).moreculling$getCullShapeElements(id) : null;
        }
        return moreculling$cullShapeElements;
    }

    @Override
    public void moreculling$setUseModelShape(boolean useModelShape) {
        this.moreculling$useModelShape = useModelShape;
    }

    @Override
    public boolean moreculling$getUseModelShape(ResourceLocation id) {
        return this.moreculling$useModelShape;
    }

    @Override
    public BlockElementFace moreculling$modifyElementFace(BlockElementFace elementFace) {
        return elementFace;
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/GsonBuilder;create()Lcom/google/gson/Gson;"
            )
    )
    private static Gson moreculling$registerCustomTypeAdapter(GsonBuilder builder) {
        return builder.registerTypeAdapter(CullShapeElement.class, new CullShapeElement.Deserializer()).create();
    }

    @Redirect(
            method = {"bake(Lnet/minecraft/client/resources/model/ModelBaker;" +
                    "Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/resources/model/ModelState;Z)" +
                    "Lnet/minecraft/client/resources/model/BakedModel;", "bakeVanilla"},
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object moreculling$overrideFaceData(Map<Direction, BlockElementFace> map, Object direction) {
        return moreculling$modifyElementFace(map.get((Direction) direction));
    }

    @Inject(
            method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;" +
                    "Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/resources/model/ModelState;Z)" +
                    "Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(
                    value = "RETURN"
            )
    )
    private void moreculling$onBake(ModelBaker baker, BlockModel parent,
                                    Function<Material, TextureAtlasSprite> textureGetter, ModelState settings,
                                    boolean hasDepth, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        if (bakedModel == null) {
            return;
        }
        BakedOpacity bakedOpacity = (BakedOpacity) bakedModel;
        if (!bakedOpacity.moreculling$canSetCullingShape()) {
            return;
        }
        ResourceLocation id = parent.parentLocation;
        if (moreculling$getUseModelShape(id)) {
            List<BlockElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = Shapes.empty();
                for (BlockElement e : modelElementList) {
                    if (e.rotation == null || e.rotation.angle() == 0) {
                        VoxelShape shape = Block.box(
                                e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z
                        );
                        voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                    }
                }

                if (settings.getRotation() != Transformation.identity()) {;
                    Direction direction = Direction.rotate(settings.getRotation().getMatrix(), Direction.NORTH);
                    if (direction.getAxis() != Direction.Axis.Y) {
                        voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, direction, voxelShape);
                    } else {
                        voxelShape = null;
                        /*direction = Direction.rotate(settings.getRotation().getMatrix(), Direction.UP); TODO rotation for non horizontal directions
                        if (direction.getAxis() != Direction.Axis.X) {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.UP, direction, voxelShape);
                        } else {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, direction, voxelShape);
                        }*/
                    }
                }

                bakedOpacity.moreculling$setCullingShape(voxelShape);
                return;
            }
        } else {
            List<CullShapeElement> cullShapeElementList = moreculling$getCullShapeElements(id);
            if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
                VoxelShape voxelShape = Shapes.empty();
                for (CullShapeElement e : cullShapeElementList) {
                    VoxelShape shape = Block.box(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                }
                bakedOpacity.moreculling$setCullingShape(voxelShape);
                return;
            }
        }
        bakedOpacity.moreculling$setCullingShape(null);
    }
}
