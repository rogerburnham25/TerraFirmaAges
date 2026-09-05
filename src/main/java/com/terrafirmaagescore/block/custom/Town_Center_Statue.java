package com.terrafirmaagescore.block.custom;


//import com.terrafirmaagescore.block.custom.CityRadiusBlockEntity;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.terrafirmaagescore.client.screen.TownNameScreen;
import net.minecraft.client.Minecraft;
import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;
import net.minecraft.world.phys.AABB;
import com.terrafirmaagescore.util.RoadDetector;
import com.terrafirmaagescore.util.ColonyNetworkManager;
import java.util.List;
//import static com.terrafirmaagescore.Block.custom.TownCenterBlockEntity.roads;


public class Town_Center_Statue extends Block implements EntityBlock {
        public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        //private int Colonies;
        // public final int count;

    public Town_Center_Statue(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TerraFirmaAgesCore.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TerraFirmaAgesCore.MODID);

    public static final DeferredHolder<Block, Town_Center_Statue> TOWN_CENTER_STATUE =
        BLOCKS.register(
                "town_center_statue",
                () -> new Town_Center_Statue(
                        BlockBehaviour.Properties.of()
                                .noOcclusion()
                                .strength(2.0F)
                )
        );

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
                BlockPos pos = context.getClickedPos();
                Level level = context.getLevel();
                // Check if the block above is air so the top half fits
                if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
                        //Colonies++;
                        return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER);
                }
                return null;
        }

        @Override
        public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
                if (!level.isClientSide()) {
                        //Colonies--;
                        DoubleBlockHalf half = state.getValue(HALF);
                        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
                        BlockState otherState = level.getBlockState(otherPos);
                        // Break the other half if it matches this block
                        if (otherState.is(this) && otherState.getValue(HALF) != half) {
                                level.setBlock(otherPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 35);
                                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
                        }
                }
                return super.playerWillDestroy(level, pos, state, player);
        }

        @Nullable
        public static BlockPos getOtherHalfPos(BlockPos pos, BlockState state) {
                return state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        }

        public static boolean territoriesOverlap(Level level, BlockPos newPos) {
                int COLONY_RADIUS = 100;
                int searchRadius = COLONY_RADIUS * 2;
                int squaredRadius = searchRadius * searchRadius;

                int newX = newPos.getX();
                //int newY = newPos.getY();
                int newZ = newPos.getZ();

                for (BlockPos pos : BlockPos.betweenClosed(
                        newPos.offset(-searchRadius, -10, -searchRadius),
                        newPos.offset(searchRadius, 10, searchRadius))) {

                        if (pos.getX() == newX && pos.getZ() == newZ) {
                                continue;
                        }

                        if (level.getBlockState(pos).is(com.terrafirmaagescore.block.custom.Town_Center_Statue.TOWN_CENTER_STATUE.get())) {
            
                                double dx = newX - pos.getX();
                                double dz = newZ - pos.getZ();
                                double distanceSquared = (dx * dx) + (dz * dz);

                                if (distanceSquared < squaredRadius) {
                                        return true; 
                                }
                        }
                }
                return false; 
        } 

        public static int colonistsInColony(Level level, BlockPos newPos) {
                int COLONY_RADIUS = 100;
                double squaredRadius = (double) COLONY_RADIUS * COLONY_RADIUS;

                AABB searchBox = new AABB(newPos).inflate(COLONY_RADIUS, 10, COLONY_RADIUS);

                List<? extends NeolithicColonistEntity> colonists = level.getEntitiesOfClass(
                        NeolithicColonistEntity.class,
                        searchBox,
                        colonist -> true
                );

                int count = 0;

                for (NeolithicColonistEntity colonist : colonists) {
                        double distanceSquared = colonist.distanceToSqr(newPos.getX(), newPos.getY(), newPos.getZ());

                        if (distanceSquared <= squaredRadius) {
                                count++;
                        }
                }

                return count;
        } 

        @Override
        public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
                if (!level.isClientSide()) {
                        if (territoriesOverlap(level, pos)) {
                                level.destroyBlock(pos, true);
                                level.destroyBlock(pos.above(), true);
                                return;
                        }
                        BlockPos abovePos = pos.above();
                        level.setBlock(abovePos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 2);
                        level.blockUpdated(pos, this);

                        if (state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                                ColonyNetworkManager.registerStatue(pos);
                                //System.out.println("[TownMod] Successfully registered statue LOWER base block at: " + pos.toShortString());
                        }
                }
                super.setPlacedBy(level, pos, state, placer, stack);
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                if (!state.is(newState.getBlock())) {
                        if (!level.isClientSide()) {
            // Automatically stop tracking this statue when broken
                                if (state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                                        ColonyNetworkManager.unregisterStatue(pos);
                                        System.out.println("[TownMod] Unregistered statue LOWER base block at: " + pos.toShortString());
                                }
                        }
                }
                super.onRemove(state, level, pos, newState, isMoving);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
                if (level.isClientSide()) {
                        if (level.getBlockEntity(pos) instanceof TownCenterBlockEntity blockEntity) {
                                TownData data = blockEntity.importDataFromTextFile();

                                String nameToSend = "unnamed_town";
                                int popToSend = 0;

                                if (data != null) {
                                        nameToSend = data.getTownName();
                                        popToSend = data.getPopulation();
                                }

                                Minecraft.getInstance().setScreen(new TownNameScreen(
                                        blockEntity,
                                        nameToSend,
                                        popToSend
                                ));
                        }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                return new TownCenterBlockEntity(pos, state);
        }
        
    // public static void register(IEventBus eventBus) {
    //     BLOCKS.register(eventBus);
    // }
    
    public static final DeferredHolder<Item, BlockItem> TOWN_CENTER_STATUE_ITEM = ITEMS.register(
            "town_center_statue",
            () -> new BlockItem(
                    TOWN_CENTER_STATUE.get(),
                    new Item.Properties()
            )
    );
}