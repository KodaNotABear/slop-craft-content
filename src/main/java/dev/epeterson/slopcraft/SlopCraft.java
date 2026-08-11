package dev.epeterson.slopcraft;

import dev.epeterson.slopcraft.index.OmniumArmorMaterials;
import dev.epeterson.slopcraft.index.SlopCraftBlocks;
import dev.epeterson.slopcraft.index.OmniumItems;
import dev.epeterson.slopcraft.index.OmniumTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import dev.epeterson.slopcraft.content.SelfCharging;

@Mod(SlopCraft.MOD_ID)
public class SlopCraft {
    public static final String MOD_ID = "slopcraft";

    public SlopCraft(ModContainer container, IEventBus modBus) {
        SlopCraftBlocks.register(modBus);
        OmniumArmorMaterials.register(modBus);
        OmniumItems.register(modBus);
        OmniumTab.register(modBus);
        NeoForge.EVENT_BUS.addListener(SelfCharging::onPlayerTick);
    }
}
