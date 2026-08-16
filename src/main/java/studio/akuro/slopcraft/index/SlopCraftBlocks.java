package studio.akuro.slopcraft.index;

import studio.akuro.slopcraft.SlopCraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SlopCraftBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SlopCraft.MOD_ID);

    // Domain-expansion wall: the texture is pitch black (black pixels stay black
    // under any light), while the block itself emits full light — a room built
    // from it is sourcelessly bright inside walls that read as void.
    public static final DeferredBlock<Block> VOID_BLOCK =
            BLOCKS.registerBlock("void_block", Block::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0F, 12.0F)
                    .lightLevel(state -> 15)
                    .sound(SoundType.DEEPSLATE));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
