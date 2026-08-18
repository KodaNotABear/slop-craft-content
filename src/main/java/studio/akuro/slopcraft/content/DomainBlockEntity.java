package studio.akuro.slopcraft.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import studio.akuro.slopcraft.index.SlopCraftBlockEntities;

public class DomainBlockEntity extends BlockEntity {
    private int ticksLeft = 200;

    public DomainBlockEntity(BlockPos pos, BlockState state) {
        super(SlopCraftBlockEntities.DOMAIN.get(), pos, state);
    }

    public void setLifespan(int ticks) {
        this.ticksLeft = ticks;
    }

    public void tick(Level level, BlockPos pos) {
        if (--ticksLeft <= 0) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ticksLeft", ticksLeft);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ticksLeft = tag.getInt("ticksLeft");
    }
}
