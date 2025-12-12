package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class BlockModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Unique
    @Nullable
    private List<CullShapeElement> moreculling$cullShapeElements = null;

    @Unique
    private boolean moreculling$useModelShape = false;
    @Unique
    private boolean moreculling$hasAutoModelShape = true;

    @Override
    public void moreculling$setCullShapeElements(@Nullable List<CullShapeElement> cullShapeElements) {
        this.moreculling$cullShapeElements = cullShapeElements;
    }

    @Override
    public @Nullable List<CullShapeElement> moreculling$getCullShapeElements(ResolvedModel parent) {
        if (this.moreculling$cullShapeElements == null) {
            return parent != null && parent.wrapped() instanceof BlockModel ?
                    ((ExtendedUnbakedModel) parent.wrapped()).moreculling$getCullShapeElements(parent.parent()) : null;
        }
        return moreculling$cullShapeElements;
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
    public void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {
        this.moreculling$hasAutoModelShape = hasAutoModelShape;
    }

    @Override
    public boolean moreculling$getHasAutoModelShape() {
        return this.moreculling$hasAutoModelShape;
    }

    @Override
    public BlockElementFace moreculling$modifyElementFace(BlockElementFace elementFace) {
        return elementFace;
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/GsonBuilder;create()Lcom/google/gson/Gson;",
                    remap = false
            )
    )
    private static Gson moreculling$registerCustomTypeAdapter(GsonBuilder instance, Operation<Gson> original) {
        return original.call(instance.registerTypeAdapter(CullShapeElement.class, new CullShapeElement.Deserializer()));
    }

}
