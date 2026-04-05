package ca.fxco.moreculling.api.renderers;

import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.impl.client.renderer.QuadConsumers;

@SuppressWarnings("unstable_package")
public class CulledBlockModelConsumer extends QuadConsumers.BlockModel {
    public MorecullingBlockModelSubmit submit;

    @Override
    public void accept(MutableQuadView quad) {
        if (!submit.shouldCull(quad.cullFace() != null ? quad.cullFace() : quad.nominalFace())) {
            super.accept(quad);
        }
    }
}
