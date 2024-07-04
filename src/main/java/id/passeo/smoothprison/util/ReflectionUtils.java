package id.passeo.smoothprison.util;


import id.passeo.smoothprison.blockhandle.Blocks;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    public static Class<?> I_BLOCK_DATA_CLAZZ;
    public static Class<?> BLOCK_POSITION_CLAZZ;
    public static Class<?> BLOCKS_CLAZZ;

    static {
        try {
            I_BLOCK_DATA_CLAZZ = Class.forName("net.minecraft.world.level.block.state.IBlockData");
            BLOCK_POSITION_CLAZZ = Class.forName("net.minecraft.core.BlockPosition");
            BLOCKS_CLAZZ = Class.forName("net.minecraft.world.level.block.Blocks");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getNMSWorld(final World world) {
        try {
            final Method getHandleMethod = world.getClass().getMethod("getHandle");
            return getHandleMethod.invoke(world);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getBlockPosition(final Location location) {
        try {
            final Constructor<?> blockPositionConstructor = BLOCK_POSITION_CLAZZ.getConstructor(int.class, int.class, int.class);
            return blockPositionConstructor.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        } catch (final NoSuchMethodException | InstantiationException |
                       IllegalAccessException |
                       InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getBlockPosition(final int x, final int y, final int z) {
        try {
            final Constructor<?> blockPositionConstructor = BLOCK_POSITION_CLAZZ.getConstructor(int.class, int.class, int.class);
            return blockPositionConstructor.newInstance(x, y, z);
        } catch (final NoSuchMethodException | InstantiationException |
                       IllegalAccessException |
                       InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getNMSBlock(final Blocks block) {
        try {
            final Field blockField = BLOCKS_CLAZZ.getField(block.getData());
            return blockField.get(null);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getIBlockData(final Object block) {
        try {
            final Method getBlockDataMethod = block.getClass().getMethod("o"); // The method to get IBlockData might differ, adjust if necessary
            return getBlockDataMethod.invoke(block);
        } catch (InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


}