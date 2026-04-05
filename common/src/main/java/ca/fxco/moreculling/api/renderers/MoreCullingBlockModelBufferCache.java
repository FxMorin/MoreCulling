package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.Nullable;

public class MoreCullingBlockModelBufferCache {
	private final MultiBufferSource.BufferSource bufferSource;
	private final OutlineBufferSource outlineBufferSource;

	private int outlineColor;

	@Nullable
	private RenderType lastRenderType;
	@Nullable
	private VertexConsumer lastBuffer;
	@Nullable
	private VertexConsumer lastOutlineBuffer;

	public MoreCullingBlockModelBufferCache(MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource) {
		this.bufferSource = bufferSource;
		this.outlineBufferSource = outlineBufferSource;
	}

	public void outlineColor(int outlineColor) {
		this.outlineColor = outlineColor;
		lastRenderType = null;
	}

	public VertexConsumer getBuffer(RenderType renderType) {
		if (renderType != lastRenderType) {
			update(renderType);
		}

		return lastBuffer;
	}

	@Nullable
	public VertexConsumer getOutlineBuffer(RenderType renderType) {
		if (renderType != lastRenderType) {
			update(renderType);
		}

		return lastOutlineBuffer;
	}

	private void update(RenderType renderType) {
		lastRenderType = renderType;
		lastBuffer = bufferSource.getBuffer(renderType);

		if (outlineColor != 0) {
			outlineBufferSource.setColor(outlineColor);
			lastOutlineBuffer = outlineBufferSource.getBuffer(renderType);
		} else {
			lastOutlineBuffer = null;
		}
	}
}
