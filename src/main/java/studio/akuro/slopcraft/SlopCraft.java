package studio.akuro.slopcraft;

import studio.akuro.slopcraft.index.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import studio.akuro.slopcraft.content.SelfCharging;

@Mod(SlopCraft.MOD_ID)
public class SlopCraft {
    public static final String MOD_ID = "slopcraft";

    public SlopCraft(ModContainer container, IEventBus modBus) {
        OmniumArmorMaterials.register(modBus);
        OmniumItems.register(modBus);
        OmniumTab.register(modBus);
        NeoForge.EVENT_BUS.addListener(SelfCharging::onPlayerTick);
    }
}
