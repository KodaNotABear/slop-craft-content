package studio.akuro.slopcraft.index;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import studio.akuro.slopcraft.SlopCraft;
import studio.akuro.slopcraft.content.DomainEntity;

import java.util.function.Supplier;

public class SlopCraftEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY =
            DeferredRegister.create(Registries.ENTITY_TYPE, SlopCraft.MOD_ID);

    public static final Supplier<EntityType<DomainEntity>> DOMAIN =
            ENTITY.register("domain", () -> EntityType.Builder.of(
                    DomainEntity::new,
                    MobCategory.MISC
            ).sized(0.5f, 0.5f).build("slopcraft:domain"));

    public static void register(IEventBus bus) {
        ENTITY.register(bus);
    }
}
