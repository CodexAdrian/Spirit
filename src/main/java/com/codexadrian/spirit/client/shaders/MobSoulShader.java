package com.codexadrian.spirit.client.shaders;

import com.codexadrian.spirit.Spirit;
import com.codexadrian.spirit.event.EventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static org.lwjgl.opengl.GL11.*;

public class MobSoulShader extends ShaderProgram {

    public static MobSoulShader INSTANCE = new MobSoulShader();
    protected int time;

    public MobSoulShader() {
        super(null, new ResourceLocation(Spirit.MODID, "shaders/soul_shader.fsh"));
    }

    @Override
    public void getAllUniformLocations() {
        this.time = super.getUniformLocation("time");
    }

    @Override
    public void start() {
        super.start();
        super.loadFloat(this.time, EventHandler.ticks / 20.0f);
    }

    public static RenderType getSoulRenderType(LivingEntity entity, LivingEntityRenderer livingEntity) {
        return RenderType.create(
                "mob_soul_layer_" + entity.getDisplayName().getString(),
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                256,
                true, true,
                RenderType.CompositeState
                        .builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_SOLID_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(livingEntity.getTextureLocation(entity), false, false))
                        .setTransparencyState(new RenderStateShard.TransparencyStateShard(
                                "mob_soul_transparency_" + entity.getDisplayName().getString(),
                                () -> {
                                    glEnable(GL_BLEND);
                                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                                    MobSoulShader.INSTANCE.start();
                                    Minecraft.getInstance().getTextureManager().bindForSetup(livingEntity.getTextureLocation(entity));
                                },
                                () -> {
                                    glDisable(GL_BLEND);
                                    MobSoulShader.INSTANCE.stop();
                                }
                        ))
                        .createCompositeState(false)
        );
    }
}
