package id.passeo.smoothprison.blockhandle.v1_20_4;


import id.passeo.smoothprison.SmoothPrison;
import id.passeo.smoothprison.blockhandle.BlockHandler;
import id.passeo.smoothprison.blockhandle.Blocks;
import id.passeo.smoothprison.blockhandle.IBlockChanger;
import id.passeo.smoothprison.util.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockChanger implements IBlockChanger {

    private final BlockHandler handler;
    private final Map<String, Method> methodCache = new HashMap<>();

    public BlockChanger() {
        handler = BlockHandler.getInstance();
    }

    @Override
    public void setBlock(World world, int x, int y, int z, Blocks block, boolean applyPhysics) {
        try {
            // Get the NMS World class
            final Object nmsWorld = handler.getOrCreateNMSWorld(world);

            // Get the BlockPosition class and its constructor
            final Object blockPosition = handler.getOrCreateBlockPosition(new Location(world, x, y, z));

            // Get the Block class and its method to get IBlockData
            final Object iBlockData = handler.getOrCreateIBlockData(block);

            // Get the NMS World class method to set a block
            Method setBlockMethod = methodCache.computeIfAbsent("setBlock"+world.getName(), method -> {
                try {
                    return nmsWorld.getClass().getMethod("a", ReflectionUtils.BLOCK_POSITION_CLAZZ, ReflectionUtils.I_BLOCK_DATA_CLAZZ, int.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });

            // Invoke the method to set the block
            setBlockMethod.invoke(nmsWorld, blockPosition, iBlockData, applyPhysics ? 3 : 2);
        } catch (InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *  Set list of block using chunk section method.
     *  NOT STABLE DON'T USE (Use only if you want to fix it
     */
    @Override
    public void setBlocksChunkSection(World world, Collection<Location> locations, Blocks block, boolean applyPhysics) {
        final Object nmsWorld = ReflectionUtils.getNMSWorld(world);
        try {
            for (Location location : locations) {
                chunkSectionReflection(location, block, applyPhysics, nmsWorld);
            }
            BlockHandler.getInstance().setPacketBlocks(locations).thenRun(() -> {
                        SmoothPrison.getInstance().getLogger().info("Blocks set to " + block.getName() + " in the selected area.");
                    }
            );

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Set a block using chunk section method.
     *  NOT STABLE DON'T USE (Use only if you want to fix it
     */
    @Override
    public void setBlockChunkSection(World world, Location location, Blocks block, boolean applyPhysics) {
        final Object nmsWorld = ReflectionUtils.getNMSWorld(world);
        try {
            chunkSectionReflection(location, block, applyPhysics, nmsWorld);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void chunkSectionReflection(Location location, Blocks block, boolean applyPhysics, Object nmsWorld) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Method getChunkMethod = methodCache.computeIfAbsent("getChunk"+location.getWorld().getName(), world -> {
            try {
                return nmsWorld.getClass().getMethod("d", int.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        final Object chunk = getChunkMethod.invoke(nmsWorld, location.getBlockX() >> 4, location.getBlockZ() >> 4);
        final Object blockPosition = handler.getOrCreateBlockPosition(location);
        final Object iBlockData = handler.getOrCreateIBlockData(block);
        final Method setBlockMethod = methodCache.computeIfAbsent("setBlockCache"+location.getWorld().getName(), world -> {
            try {
                return chunk.getClass().getMethod("a", ReflectionUtils.BLOCK_POSITION_CLAZZ, ReflectionUtils.I_BLOCK_DATA_CLAZZ, boolean.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        setBlockMethod.invoke(chunk, blockPosition, iBlockData, applyPhysics);
    }

}
