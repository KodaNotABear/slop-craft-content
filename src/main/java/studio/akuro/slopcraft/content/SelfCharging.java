package studio.akuro.slopcraft.content;

import studio.akuro.slopcraft.SlopCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Keeps every item in the slopcraft:self_charging tag permanently full of
 * energy. The pack avoids FE as a player-facing system; for the handful of
 * useful mods that hard-require it (Building Gadgets), this makes the energy
 * mechanic invisible instead of forcing an FE generation chain into the pack.
 */
public final class SelfCharging {
    public static final TagKey<Item> SELF_CHARGING =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SlopCraft.MOD_ID, "self_charging"));

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }
        for (ItemStack stack : player.getInventory().items) {
            top(stack);
        }
        top(player.getInventory().offhand.getFirst());
    }

    private static void top(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(SELF_CHARGING)) {
            return;
        }
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return;
        }
        // receiveEnergy may cap per call; a few rounds tops off any sane item.
        for (int i = 0; i < 8 && energy.receiveEnergy(Integer.MAX_VALUE, false) > 0; i++) {
        }
    }

    private SelfCharging() {}
}
