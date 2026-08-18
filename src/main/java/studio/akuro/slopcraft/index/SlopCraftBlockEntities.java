package studio.akuro.slopcraft.index;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import studio.akuro.slopcraft.SlopCraft;
import studio.akuro.slopcraft.content.DomainBlockEntity;

import java.util.function.Supplier;



public class SlopCraftBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SlopCraft.MOD_ID);

    public static final Supplier<BlockEntityType<DomainBlockEntity>> DOMAIN =
            BLOCK_ENTITIES.register("domain", () -> BlockEntityType.Builder.of(
                    DomainBlockEntity::new,
                    SlopCraftBlocks.DOMAIN_WALL.get(), SlopCraftBlocks.VOID_LIGHT.get()
                    ).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

}
