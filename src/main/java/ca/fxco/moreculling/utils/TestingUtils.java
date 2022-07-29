package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

import java.util.List;

public class TestingUtils {

    public static void testBlockStateTranslucency(BlockState state, boolean passState) {
        BakedModel model = CullingUtils.getBakedModel(state);
        boolean hasTranslucency = ((BakedOpacity)model).hasTextureTranslucency(state);
        if (!hasTranslucency) return; // Only print if failing to cull due to translucency
        String[] classPackage = model.getClass().getName().split("\\.");
        System.out.println(state.getBlock().getName() + " -> " + classPackage[classPackage.length - 1]);
        List<BakedModel> models = ((BakedOpacity)model).getModels();
        if (models != null) {
            for (BakedModel m : models) {
                // If BakedModel texture is translucent we figure out why here. Test each one individually
                if (((BakedOpacity) m).hasTextureTranslucency(passState ? state : null)) {
                    // Check main sprite (May not be used in the future)
                    System.out.println("Failed: "+m.getClass().getName());
                    if (((SpriteOpacity)m.getParticleSprite()).hasTranslucency()) {
                        SpriteUtils.printOpacity("sprite",m.getParticleSprite());
                    }
                    // Check Quads
                    List<BakedQuad> bakedQuads = m.getQuads(null, null, null);
                    for (BakedQuad quad : bakedQuads)
                        if (((SpriteOpacity)quad.getSprite()).hasTranslucency())
                            SpriteUtils.printOpacity("quad",quad.getSprite());
                    // Check Face Quads
                    for (Direction dir : Direction.values()) {
                        List<BakedQuad> bakedFaceQuads = m.getQuads(null, dir, null);
                        for (BakedQuad quad : bakedFaceQuads) {
                            if (((SpriteOpacity) quad.getSprite()).hasTranslucency()) {
                                SpriteUtils.printOpacity("FaceQuad - dir: " + dir,quad.getSprite());
                            }
                        }
                    }
                }
            }
        }
    }
}
