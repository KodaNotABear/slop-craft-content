package studio.akuro.slopcraft.content;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import studio.akuro.slopcraft.index.SlopCraftBlocks;

public class DomainEntity extends EntityProjectileSpell {
    private int r;
    private int layer;

    public DomainEntity(EntityType<? extends EntityProjectileSpell> entityType, Level level) {
        super(entityType, level);
    }

    public void configure(int radius) {
        this.r = radius;
        this.layer = radius;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("layer", layer);
        tag.putInt("r", r);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        layer = tag.getInt("layer");
        r = tag.getInt("r");
    }

    private void place(BlockPos pos, Block block) {
        if (!level().getBlockState(pos).isAir()) return;

        level().setBlockAndUpdate(pos, block.defaultBlockState());

        if (level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.setLifespan(200 + (layer + r) * 2);
        }
    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            return;
        }

        BlockPos center = blockPosition();
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                double dist = Math.sqrt(dx * dx + layer * layer + dz * dz);
                BlockPos pos = center.offset(dx, layer, dz);

                if (Math.round(dist) == r) {
                    place(pos, SlopCraftBlocks.DOMAIN_WALL.get());
                }
                if (dist < r && layer % 3 == 0 && dx % 3 == 0 && dz % 3 == 0) {
                    place(pos, SlopCraftBlocks.VOID_LIGHT.get());
                }
            }
        }
        layer--;
        if (layer < -r ) discard();
    }
}
