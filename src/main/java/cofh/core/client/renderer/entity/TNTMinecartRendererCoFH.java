package cofh.core.client.renderer.entity;

import cofh.core.common.entity.AbstractTNTMinecart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TNTMinecartRendererCoFH extends MinecartRenderer<AbstractTNTMinecart> {

    private final BlockRenderDispatcher blockRenderer;

    public TNTMinecartRendererCoFH(EntityRendererProvider.Context renderManagerIn) {

        super(renderManagerIn, ModelLayers.TNT_MINECART);
        this.blockRenderer = renderManagerIn.getBlockRenderDispatcher();
    }

    @Override
    protected void renderMinecartContents(AbstractTNTMinecart entityIn, float partialTicks, BlockState stateIn, PoseStack poseStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        int i = entityIn.getFuseTicks();
        if (i > -1 && (float) i - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) i - partialTicks + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            poseStackIn.scale(f1, f1, f1);
        }
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, stateIn, poseStackIn, bufferIn, packedLightIn, i > -1 && i / 5 % 2 == 0);
    }

}
