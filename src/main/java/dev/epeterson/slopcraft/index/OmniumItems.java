package dev.epeterson.slopcraft.index;

import dev.epeterson.slopcraft.SlopCraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class OmniumItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlopCraft.MOD_ID);

    // Slightly past netherite on every axis; the material is refined FROM
    // netherite gear via smithing, so it must never feel like a sidegrade.
    public static final Tier CRUDE_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2400, 10.0F, 4.5F, 16,
            () -> Ingredient.of(OmniumItems.CRUDE_OMNIUM.get()));

    public static final DeferredItem<Item> CRUDE_OMNIUM =
            ITEMS.registerSimpleItem("crude_omnium", new Item.Properties().fireResistant());

    public static final DeferredItem<Item> CRUDE_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.register("crude_upgrade_smithing_template", () -> new SmithingTemplateItem(
                    Component.translatable("upgrade.slopcraft.crude_upgrade.applies_to"),
                    Component.translatable("upgrade.slopcraft.crude_upgrade.ingredients"),
                    Component.translatable("upgrade.slopcraft.crude_upgrade"),
                    Component.translatable("upgrade.slopcraft.crude_upgrade.base_slot_description"),
                    Component.translatable("upgrade.slopcraft.crude_upgrade.additions_slot_description"),
                    List.of(
                            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
                            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
                            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
                            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
                            ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
                            ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe"),
                            ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
                            ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"),
                            ResourceLocation.withDefaultNamespace("item/empty_slot_hoe")),
                    List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"))));

    public static final DeferredItem<SwordItem> CRUDE_SWORD =
            ITEMS.register("crude_sword", () -> new SwordItem(CRUDE_TIER, new Item.Properties()
                    .fireResistant()
                    .attributes(SwordItem.createAttributes(CRUDE_TIER, 3, -2.4F))));

    public static final DeferredItem<PickaxeItem> CRUDE_PICKAXE =
            ITEMS.register("crude_pickaxe", () -> new PickaxeItem(CRUDE_TIER, new Item.Properties()
                    .fireResistant()
                    .attributes(PickaxeItem.createAttributes(CRUDE_TIER, 1.0F, -2.8F))));

    public static final DeferredItem<AxeItem> CRUDE_AXE =
            ITEMS.register("crude_axe", () -> new AxeItem(CRUDE_TIER, new Item.Properties()
                    .fireResistant()
                    .attributes(AxeItem.createAttributes(CRUDE_TIER, 5.0F, -3.0F))));

    public static final DeferredItem<ShovelItem> CRUDE_SHOVEL =
            ITEMS.register("crude_shovel", () -> new ShovelItem(CRUDE_TIER, new Item.Properties()
                    .fireResistant()
                    .attributes(ShovelItem.createAttributes(CRUDE_TIER, 1.5F, -3.0F))));

    public static final DeferredItem<HoeItem> CRUDE_HOE =
            ITEMS.register("crude_hoe", () -> new HoeItem(CRUDE_TIER, new Item.Properties()
                    .fireResistant()
                    .attributes(HoeItem.createAttributes(CRUDE_TIER, -4.0F, 0.0F))));

    public static final DeferredItem<ArmorItem> CRUDE_HELMET =
            ITEMS.register("crude_helmet", () -> new ArmorItem(
                    OmniumArmorMaterials.CRUDE_OMNIUM, ArmorItem.Type.HELMET,
                    new Item.Properties().fireResistant()
                            .durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> CRUDE_CHESTPLATE =
            ITEMS.register("crude_chestplate", () -> new ArmorItem(
                    OmniumArmorMaterials.CRUDE_OMNIUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().fireResistant()
                            .durability(ArmorItem.Type.CHESTPLATE.getDurability(40))));

    public static final DeferredItem<ArmorItem> CRUDE_LEGGINGS =
            ITEMS.register("crude_leggings", () -> new ArmorItem(
                    OmniumArmorMaterials.CRUDE_OMNIUM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().fireResistant()
                            .durability(ArmorItem.Type.LEGGINGS.getDurability(40))));

    public static final DeferredItem<ArmorItem> CRUDE_BOOTS =
            ITEMS.register("crude_boots", () -> new ArmorItem(
                    OmniumArmorMaterials.CRUDE_OMNIUM, ArmorItem.Type.BOOTS,
                    new Item.Properties().fireResistant()
                            .durability(ArmorItem.Type.BOOTS.getDurability(40))));

    public static final DeferredItem<net.minecraft.world.item.BlockItem> VOID_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(SlopCraftBlocks.VOID_BLOCK);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
