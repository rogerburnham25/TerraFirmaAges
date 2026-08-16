package com.terrafirmaagescore.entity.custom;

import javax.annotation.Nullable;

import com.terrafirmaagescore.entity.ModEntities;
import com.terrafirmaagescore.ai.HarvestGoal;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
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

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animatable.GeoEntity;
import net.minecraft.core.BlockPos;

import java.util.List;

public class NeolithicColonistEntity extends Animal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NeolithicColonistEntity(EntityType<NeolithicColonistEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public double getTick(Object relatedObject) {
        return this.tickCount;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "WalkOrRun", 0, state -> {

            if (this.isHarvesting()) {
                return PlayState.STOP;
            }

            double speed = this.getDeltaMovement().horizontalDistance();

            if (!state.isMoving()) {
                return PlayState.STOP;
            }

            if (speed > 0.08D) {
                state.setAnimation(RUN);
                state.getController().setAnimationSpeed(2.5);
            } else {
                state.setAnimation(WALK);
                state.getController().setAnimationSpeed(1.5);
            }
            

            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "actions", state -> PlayState.STOP).triggerableAnim("harvest", Harvest_Crop));
    }
    private BlockPos harvestTarget;

    public void setHarvestTarget(BlockPos pos) {
        this.harvestTarget = pos;
    }

    public BlockPos getHarvestTarget() {
        return this.harvestTarget;
    }

    public boolean hasHarvestTarget() {
        return this.harvestTarget != null;
    }

    public void clearHarvestTarget() {
        this.harvestTarget = null;
    }


    private boolean harvesting = false;

    private int harvestTicks = 0;

    public void startHarvestAnimation() {
        harvesting = true;
        harvestTicks = 40; // 2 seconds
    }

    public boolean isHarvesting() {
        return this.harvesting;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0F));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0F,
                stack -> stack.is(Items.BEDROCK), false));
                
        this.goalSelector.addGoal(4, new HarvestGoal(this));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25F));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F, 1.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 14d)
                .add(Attributes.MOVEMENT_SPEED, 0.25d)
                .add(Attributes.FOLLOW_RANGE, 20d);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.ACACIA_BOAT);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.NEOLITHIC_COLONIST.get().create(level);
    }

    private static final RawAnimation WALK =
    RawAnimation.begin().thenLoop("Colonist_walk");

    private static final RawAnimation RUN =
    RawAnimation.begin().thenLoop("Colonist_Run");

    private static final RawAnimation Harvest_Crop =
    RawAnimation.begin().thenPlay("Harvest_Crop");

    private int panicTicks = 0;

    public void panic(int ticks) {
        panicTicks = ticks;
    }

    public boolean isPanicking() {
        return panicTicks > 0;
    }

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

    private final ItemStackHandler inventory = new ItemStackHandler(9);

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
    