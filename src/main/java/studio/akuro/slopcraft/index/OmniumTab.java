package studio.akuro.slopcraft.index;

import studio.akuro.slopcraft.SlopCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OmniumTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlopCraft.MOD_ID);

    static {
        TABS.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.slopcraft"))
                .icon(() -> new ItemStack(OmniumItems.OMNIUM.get()))
                .displayItems((params, output) -> {
                    // Registration order is curated: materials, templates,
                    // then gear per stage, then blocks.
                    for (DeferredHolder<Item, ? extends Item> entry : OmniumItems.ITEMS.getEntries()) {
                        output.accept(entry.get());
                    }
                })
                .build());
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
