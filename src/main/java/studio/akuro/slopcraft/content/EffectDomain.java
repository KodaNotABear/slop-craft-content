package studio.akuro.slopcraft.content;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import studio.akuro.slopcraft.index.SlopCraftBlocks;
import studio.akuro.slopcraft.index.SlopCraftEntities;

import java.util.Set;

public class EffectDomain extends AbstractEffect {
    public static final int RADIUS = 10;
    public static final EffectDomain INSTANCE = new EffectDomain();
    private EffectDomain() {
        super(ResourceLocation.fromNamespaceAndPath("slopcraft", "glyph_domain"), "Deploy Domain");
    }

    @Override
    protected int getDefaultManaCost() {
        return 400;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Creates a spherical domain around the target while constantly activating the spell's effects inside its area.";
    }

    @Override
    public @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }

    private void deploy(BlockPos center, Level world, LivingEntity shooter, SpellContext spellContext, SpellResolver spellResolver) {

        Spell remainder = spellContext.getRemainingSpell();

        spellContext.setCanceled(true);

        DomainEntity domain = new DomainEntity(SlopCraftEntities.DOMAIN.get(), world);
        domain.setPos(Vec3.atCenterOf(center));
        domain.configure(RADIUS);
        world.addFreshEntity(domain);

        if (!remainder.isEmpty()) {
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(center).inflate(RADIUS))) {
                if (entity == shooter) {
                    continue;
                } else {
                    SpellContext newContext = spellContext.clone().withSpell(remainder);
                    spellResolver.getNewResolver(newContext).onResolveEffect(world, new EntityHitResult(entity));
                }
            }
        }

    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        deploy(rayTraceResult.getBlockPos(), world, shooter, spellContext, resolver);

    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver) {
        deploy(rayTraceResult.getEntity().blockPosition(), world, shooter, spellContext, spellResolver);
    }
}