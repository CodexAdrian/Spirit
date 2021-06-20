package me.codexadrian.spirit.mixin;

import me.codexadrian.spirit.Corrupted;
import me.codexadrian.spirit.client.shaders.MobSoulShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class EntityRendererMixin {
    private LivingEntity currentlyRendered;

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (((Corrupted) livingEntity).isCorrupted()) {
            currentlyRendered = livingEntity;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void postRender(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (currentlyRendered != null) {
            currentlyRendered = null;
        }
    }
    
    @Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
    private void getRenderType(LivingEntity livingEntity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderType> cir){
        if (((Corrupted) livingEntity).isCorrupted()) {
            cir.setReturnValue(MobSoulShaders.getSoulRenderType(livingEntity, (LivingEntityRenderer) (Object)this));
        }
    }
/*
    @ModifyConstant(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")), constant = @Constant(floatValue = 1.0f, ordinal = 0))
    private float getRed(float original) {
        return getColor(0.2f, original);
    }

    @ModifyConstant(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")), constant = @Constant(floatValue = 1.0f, ordinal = 1))
    private float getGreen(float original) {
        return getColor(0.97f, original);
    }
    
    @ModifyConstant(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")), constant = @Constant(floatValue = 0.15f, ordinal = 0))
    private float getAlphaInvisible(float original) {
        return getAlpha(original);
    }

    @ModifyConstant(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")), constant = @Constant(floatValue = 1f, ordinal = 3))
    private float getAlphaVisible(float original) {
        return getAlpha(original);
    }

    private float getAlpha(float original) {
        return getColor(0.3f, original);
    }
    */

    private float getColor(float color, float original) {
        return currentlyRendered != null ? original * color : original;
    }
}

