package me.codexadrian.spirit;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cottonmc.jankson.JanksonFactory;
import me.codexadrian.spirit.blocks.soulcage.SoulCageRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.system.CallbackI;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpiritClient implements ClientModInitializer {
    private static SpiritClientConfig clientConfig;
    public static final Jankson jankson = JanksonFactory.createJankson();

    @Override
    public void onInitializeClient() {
        try {
            clientConfig = SpiritClientConfig.loadConfig(FabricLoader.getInstance().getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BlockRenderLayerMap.INSTANCE.putBlock(Spirit.SOUL_CAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spirit.BROKEN_SPAWNER, RenderType.cutout());
        BlockEntityRendererRegistry.INSTANCE.register(Spirit.SOUL_CAGE_ENTITY, SoulCageRenderer::new);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("spirit_reload");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
            
            }
        });
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                int red = 0xC4;
                int green = 0xFF;
                int blue = 0xFE;
                if (stack.hasTag()) {
                    float percentage = Math.min(stack.getTag().getCompound("StoredEntity").getInt("Souls") / (float) Spirit.getSpiritConfig().getMaxSouls(), 1f);
                    red -= percentage * 91;
                    green -= percentage * 7;
                    blue += percentage;
                }
                return red << 16 | green << 8 | blue;
            } else return -1;
        }, Spirit.SOUL_CRYSTAL);
        FabricModelPredicateProviderRegistry.register(Spirit.SOUL_CRYSTAL, new ResourceLocation(Spirit.MODID, "activation"), (stack, level, entity, seed) -> stack.hasTag() ? Spirit.getTier(stack) / (float) 4 : 0);
    }
    public static SpiritClientConfig getClientConfig() {
        return clientConfig;
    }

    public static void saveConfig(SpiritClientConfig config) {
        try {
            Path file = FabricLoader.getInstance().getConfigDir().resolve("spirit_client.json");

            JsonElement json = jankson.toJson(config);
            String result = json.toJson(true, true);
            Files.write(file, result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
