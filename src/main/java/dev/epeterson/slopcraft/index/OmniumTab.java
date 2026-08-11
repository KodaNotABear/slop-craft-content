package dev.epeterson.slopcraft.index;

import dev.epeterson.slopcraft.SlopCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OmniumTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlopCraft.MOD_ID);

    static {
        TABS.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.slopcraft"))
                .icon(() -> new ItemStack(OmniumItems.CRUDE_OMNIUM.get()))
                .displayItems((params, output) -> {
                    output.accept(SlopCraftBlocks.VOID_BLOCK.get());
                    output.accept(OmniumItems.CRUDE_OMNIUM.get());
                    output.accept(OmniumItems.CRUDE_UPGRADE_SMITHING_TEMPLATE.get());
                    output.accept(OmniumItems.CRUDE_SWORD.get());
                    output.accept(OmniumItems.CRUDE_PICKAXE.get());
                    output.accept(OmniumItems.CRUDE_AXE.get());
                    output.accept(OmniumItems.CRUDE_SHOVEL.get());
                    output.accept(OmniumItems.CRUDE_HOE.get());
                    output.accept(OmniumItems.CRUDE_HELMET.get());
                    output.accept(OmniumItems.CRUDE_CHESTPLATE.get());
                    output.accept(OmniumItems.CRUDE_LEGGINGS.get());
                    output.accept(OmniumItems.CRUDE_BOOTS.get());
                })
                .build());
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
