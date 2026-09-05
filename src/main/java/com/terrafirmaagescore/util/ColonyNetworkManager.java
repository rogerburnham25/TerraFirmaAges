package com.terrafirmaagescore.util;

import net.minecraft.core.BlockPos;
import java.util.HashSet;
import java.util.Set;

public class ColonyNetworkManager {
    // A global, fast-lookup set of all active statue coordinates
    private static final Set<BlockPos> ACTIVE_STATUES = new HashSet<>();

    public static void registerStatue(BlockPos pos) {
        ACTIVE_STATUES.add(pos.immutable());
    }

    public static void unregisterStatue(BlockPos pos) {
        ACTIVE_STATUES.remove(pos);
    }

    public static Set<BlockPos> getActiveStatues() {
        return ACTIVE_STATUES;
    }
}
