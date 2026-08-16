package studio.akuro.slopcraft.index;

import studio.akuro.slopcraft.SlopCraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
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
import java.util.function.Supplier;

public class OmniumItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlopCraft.MOD_ID);

    // One lineage, three refinement states. Each tier is a modest step past
    // the last - a reforge, not a replacement - and repairs with its own state
    // of the material.
    public static final Tier CRUDE_TIER = tier(2400, 10.0F, 4.5F, 16, () -> OmniumItems.CRUDE_OMNIUM.get());
    public static final Tier ATTUNED_TIER = tier(2800, 11.0F, 5.0F, 18, () -> OmniumItems.ATTUNED_OMNIUM.get());
    public static final Tier OMNIUM_TIER = tier(3600, 12.0F, 5.5F, 22, () -> OmniumItems.OMNIUM.get());

    public static final DeferredItem<Item> CRUDE_OMNIUM =
            ITEMS.registerSimpleItem("crude_omnium", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> ATTUNED_OMNIUM =
            ITEMS.registerSimpleItem("attuned_omnium", new Item.Properties().fireResistant());
    public static final DeferredItem<Item> OMNIUM =
            ITEMS.registerSimpleItem("omnium", new Item.Properties().fireResistant());

    public static final DeferredItem<Item> CRUDE_UPGRADE_SMITHING_TEMPLATE = template("crude");
    public static final DeferredItem<Item> ATTUNED_UPGRADE_SMITHING_TEMPLATE = template("attuned");
    public static final DeferredItem<Item> OMNIUM_UPGRADE_SMITHING_TEMPLATE = template("omnium");

    static {
        gearSet("crude", CRUDE_TIER, OmniumArmorMaterials.CRUDE_OMNIUM, 40);
        gearSet("attuned", ATTUNED_TIER, OmniumArmorMaterials.ATTUNED_OMNIUM, 44);
        gearSet("omnium", OMNIUM_TIER, OmniumArmorMaterials.OMNIUM, 48);
    }

    private static Tier tier(int uses, float speed, float damage, int enchantValue, Supplier<Item> repair) {
        return new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, uses, speed, damage,
                enchantValue, () -> Ingredient.of(repair.get()));
    }

    private static DeferredItem<Item> template(String stage) {
        String key = "upgrade.slopcraft." + stage + "_upgrade";
        return ITEMS.register(stage + "_upgrade_smithing_template", () -> new SmithingTemplateItem(
                Component.translatable(key + ".applies_to"),
                Component.translatable(key + ".ingredients"),
                Component.translatable(key),
                Component.translatable(key + ".base_slot_description"),
                Component.translatable(key + ".additions_slot_description"),
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
    }

    private static void gearSet(String prefix, Tier tier, Holder<ArmorMaterial> material, int durabilityMultiplier) {
        ITEMS.register(prefix + "_sword", () -> new SwordItem(tier, new Item.Properties()
                .fireResistant().attributes(SwordItem.createAttributes(tier, 3, -2.4F))));
        ITEMS.register(prefix + "_pickaxe", () -> new PickaxeItem(tier, new Item.Properties()
                .fireResistant().attributes(PickaxeItem.createAttributes(tier, 1.0F, -2.8F))));
        ITEMS.register(prefix + "_axe", () -> new AxeItem(tier, new Item.Properties()
                .fireResistant().attributes(AxeItem.createAttributes(tier, 5.0F, -3.0F))));
        ITEMS.register(prefix + "_shovel", () -> new ShovelItem(tier, new Item.Properties()
                .fireResistant().attributes(ShovelItem.createAttributes(tier, 1.5F, -3.0F))));
        ITEMS.register(prefix + "_hoe", () -> new HoeItem(tier, new Item.Properties()
                .fireResistant().attributes(HoeItem.createAttributes(tier, -4.0F, 0.0F))));
        for (ArmorItem.Type type : new ArmorItem.Type[]{
                ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS}) {
            ITEMS.register(prefix + "_" + type.getName(), () -> new ArmorItem(material, type,
                    new Item.Properties().fireResistant().durability(type.getDurability(durabilityMultiplier))));
        }
    }

    public static final DeferredItem<net.minecraft.world.item.BlockItem> VOID_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(SlopCraftBlocks.VOID_BLOCK);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
