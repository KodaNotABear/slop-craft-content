package dev.epeterson.slopcraft.index;

import dev.epeterson.slopcraft.SlopCraft;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class OmniumArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, SlopCraft.MOD_ID);

    // Netherite-and-a-half: the first reforge of the Great Work. Numbers stay
    // close to netherite so the stage reads as refinement, not replacement.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CRUDE_OMNIUM =
            ARMOR_MATERIALS.register("crude_omnium", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    16,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(OmniumItems.CRUDE_OMNIUM.get()),
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(SlopCraft.MOD_ID, "crude_omnium"))),
                    3.5F,
                    0.1F
            ));

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}
