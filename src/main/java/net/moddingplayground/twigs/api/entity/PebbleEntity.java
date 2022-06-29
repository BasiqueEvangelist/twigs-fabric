package net.moddingplayground.twigs.api.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.moddingplayground.twigs.api.item.TwigsItems;
import net.moddingplayground.twigs.api.particle.TwigsParticleTypes;

public class PebbleEntity extends ThrownItemEntity {
    public PebbleEntity(EntityType<? extends PebbleEntity> type, World world) {
        super(type, world);
    }

    public PebbleEntity(World world, LivingEntity owner) {
        super(TwigsEntityType.PEBBLE, owner, world);
    }

    public PebbleEntity(World world, double x, double y, double z) {
        super(TwigsEntityType.PEBBLE, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return TwigsItems.PEBBLE;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack stack = this.getItem();
        return stack.isEmpty() ? TwigsParticleTypes.ITEM_PEBBLE : new ItemStackParticleEffect(ParticleTypes.ITEM, stack);
    }

    /* Collision */

    @Override
    protected void onCollision(HitResult hit) {
        super.onCollision(hit);

        if (!this.world.isClient) {
            ItemStack stack = this.getItem();
            this.dropStack(stack == ItemStack.EMPTY ? new ItemStack(this.getDefaultItem()) : stack);

            this.world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        Entity entity = hit.getEntity();
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.5F);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particle = this.getParticleParameters();
            for (int i = 0; i < 8; i++) this.world.addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }
}