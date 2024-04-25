package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
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

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Shadow
    @Nullable
    protected JsonUnbakedModel parent;

    @Shadow
    public abstract List<ModelElement> getElements();

    @Unique
    @Nullable
    private List<CullShapeElement> cullShapeElements = null;

    @Unique
    private boolean moreculling$useModelShape = true;

    @Override
    public void moreculling$setCullShapeElements(@Nullable List<CullShapeElement> cullShapeElements) {
        this.cullShapeElements = cullShapeElements;
    }

    @Override
    public @Nullable List<CullShapeElement> moreculling$getCullShapeElements(Identifier id) {
        if (this.cullShapeElements == null) {
            return this.parent != null ?
                    ((ExtendedUnbakedModel) this.parent).moreculling$getCullShapeElements(id) : null;
        }
        return cullShapeElements;
    }

    @Override
    public void moreculling$setUseModelShape(boolean useModelShape) {
        this.moreculling$useModelShape = useModelShape;
    }

    @Override
    public boolean moreculling$getUseModelShape(Identifier id) {
        return this.moreculling$useModelShape;
    }

    @Override
    public ModelElementFace moreculling$modifyElementFace(ModelElementFace elementFace) {
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
            method = "bake(Lnet/minecraft/client/render/model/Baker;" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)" +
                    "Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object moreculling$overrideFaceData(Map<Direction, ModelElementFace> map, Object direction) {
        return moreculling$modifyElementFace(map.get((Direction) direction));
    }

    @Inject(
            method = "bake(Lnet/minecraft/client/render/model/Baker;" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)" +
                    "Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BEFORE
            )
    )
    private void moreculling$onBake(Baker baker, JsonUnbakedModel parent,
                                    Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings,
                                    Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        if (bakedModel == null) {
            return;
        }
        BakedOpacity bakedOpacity = (BakedOpacity) bakedModel;
        if (!bakedOpacity.moreculling$canSetCullingShape()) {
            return;
        }
        if (moreculling$getUseModelShape(id) && settings.getRotation() == AffineTransformation.identity()) {
            List<ModelElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (ModelElement e : modelElementList) {
                    if (e.rotation == null || e.rotation.angle() == 0) {
                        VoxelShape shape = Block.createCuboidShape(
                                e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z
                        );
                        voxelShape = VoxelShapes.union(voxelShape, shape);
                    }
                }
                bakedOpacity.moreculling$setCullingShape(voxelShape);
                return;
            }
        } else {
            List<CullShapeElement> cullShapeElementList = moreculling$getCullShapeElements(id);
            if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (CullShapeElement e : cullShapeElementList) {
                    VoxelShape shape = Block.createCuboidShape(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = VoxelShapes.union(voxelShape, shape);
                }
                bakedOpacity.moreculling$setCullingShape(voxelShape);
                return;
            }
        }
        bakedOpacity.moreculling$setCullingShape(null);
    }
}
