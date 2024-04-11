package cofh.core.mixin;

import cofh.core.client.PostEffect;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject (
            method = "resize",
            at = @At ("TAIL")
    )
    private void resize(int width, int height, CallbackInfo ci) {

        for (PostEffect effect : PostEffect.getAllEffects()) {
            effect.resize(width, height);
        }
    }

}
