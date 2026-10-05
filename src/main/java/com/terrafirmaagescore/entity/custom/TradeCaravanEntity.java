package com.terrafirmaagescore.entity.custom;

import javax.annotation.Nullable;

import com.terrafirmaagescore.entity.ModEntities;
import com.terrafirmaagescore.ai.TradeGoal;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import net.neoforged.neoforge.items.ItemStackHandler;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animatable.GeoEntity;
import net.minecraft.core.BlockPos;

import java.util.List;

public class TradeCaravanEntity extends Animal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TradeCaravanEntity(EntityType<TradeCaravanEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public double getTick(Object relatedObject) {
        return this.tickCount;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "WalkOrRun", 0, state -> {

            double speed = this.getDeltaMovement().horizontalDistance();

            if (!state.isMoving()) {
                return PlayState.STOP;
            }

            state.getController().setAnimationSpeed(1);

            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "actions", state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 14d)
                .add(Attributes.MOVEMENT_SPEED, 0.25d)
                .add(Attributes.FOLLOW_RANGE, 20d);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.AIR);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.TRADE_CARAVAN_ENTITY.get().create(level);
    }

    private static final RawAnimation WALK =
    RawAnimation.begin().thenLoop("caravan_move");

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);

        if (damaged && !level().isClientSide()) {
            List<NeolithicColonistEntity> nearby = level().getEntitiesOfClass(
                    NeolithicColonistEntity.class,
                    getBoundingBox().inflate(16));

            for (NeolithicColonistEntity colonist : nearby) {
                colonist.panic(200); // 10 seconds
            }
        }

        return damaged;
    }

    private final ItemStackHandler inventory = new ItemStackHandler(27);

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.serializeNBT(registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registryAccess(), tag.getCompound("Inventory"));
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        for (int slot = 0; slot < this.getInventory().getSlots(); slot++) {
            ItemStack stack = this.getInventory().getStackInSlot(slot);

            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
                this.getInventory().setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }
}
    