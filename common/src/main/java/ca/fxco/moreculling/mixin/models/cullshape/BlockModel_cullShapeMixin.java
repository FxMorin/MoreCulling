package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class BlockModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Shadow
    @Nullable
    private UnbakedModel parent;

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
    public @Nullable List<CullShapeElement> moreculling$getCullShapeElements(ResourceLocation id) {
        if (this.moreculling$cullShapeElements == null) {
            return this.parent instanceof BlockModel ?
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

}
