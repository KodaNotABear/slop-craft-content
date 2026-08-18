package studio.akuro.slopcraft.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import studio.akuro.slopcraft.SlopCraft;
import studio.akuro.slopcraft.index.SlopCraftEntities;

@EventBusSubscriber (modid = SlopCraft.MOD_ID, value = Dist.CLIENT)

public class SlopCraftClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SlopCraftEntities.DOMAIN.get(), NoopRenderer::new);
    }
}
