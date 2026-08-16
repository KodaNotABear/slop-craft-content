package studio.akuro.slopcraft.index;

import studio.akuro.slopcraft.SlopCraft;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class OmniumArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, SlopCraft.MOD_ID);

    // Stage progression stays close to netherite so each reforge reads as
    // refinement; only the terminal stage steps protection up a point.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CRUDE_OMNIUM =
            register("crude_omnium", 3, 8, 6, 3, 11, 16, 3.5F, 0.1F,
                    SoundEvents.ARMOR_EQUIP_NETHERITE, () -> OmniumItems.CRUDE_OMNIUM.get());

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ATTUNED_OMNIUM =
            register("attuned_omnium", 3, 8, 6, 3, 11, 18, 4.0F, 0.1F,
                    SoundEvents.ARMOR_EQUIP_NETHERITE, () -> OmniumItems.ATTUNED_OMNIUM.get());

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> OMNIUM =
            register("omnium", 4, 9, 7, 4, 13, 22, 4.5F, 0.15F,
                    SoundEvents.ARMOR_EQUIP_NETHERITE, () -> OmniumItems.OMNIUM.get());

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name, int boots, int chest, int legs, int helmet, int body,
            int enchantValue, float toughness, float knockbackResistance,
            Holder<SoundEvent> equipSound, Supplier<Item> repairItem) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, boots);
                    map.put(ArmorItem.Type.LEGGINGS, legs);
                    map.put(ArmorItem.Type.CHESTPLATE, chest);
                    map.put(ArmorItem.Type.HELMET, helmet);
                    map.put(ArmorItem.Type.BODY, body);
                }),
                enchantValue,
                equipSound,
                () -> Ingredient.of(repairItem.get()),
                List.of(new ArmorMaterial.Layer(
                        ResourceLocation.fromNamespaceAndPath(SlopCraft.MOD_ID, name))),
                toughness,
                knockbackResistance
        ));
    }

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}
