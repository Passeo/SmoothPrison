package id.passeo.smoothprison.blockhandle;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import id.passeo.smoothprison.SmoothPrison;
import id.passeo.smoothprison.blockhandle.packet.BlockChangePacket;
import id.passeo.smoothprison.blockhandle.workload.BlockSetChunkSectionWorkload;
import id.passeo.smoothprison.blockhandle.workload.BlockSetWorkload;
import id.passeo.smoothprison.blockhandle.workload.WorkloadRunnable;
import id.passeo.smoothprison.util.PacketUtils;
import id.passeo.smoothprison.util.ProbabilitySelector;
import id.passeo.smoothprison.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BlockHandler {

    private static BlockHandler instance;
    private final Map<Blocks, Object> iBlockDataCache = new HashMap<>();
    private final Map<World, Object> nmsWorldCache = new HashMap<>();
    private final Map<Location, Object> blockPositionCache = new HashMap<>();
    private final Map<Blocks, Object> nmsBlockCache = new HashMap<>();
    private final Map<Blocks, Double> probabilityBlock = new HashMap<>();

    public BlockHandler() {
        instance = this;
        probabilityBlock.put(Blocks.STONE, 0.7);
        probabilityBlock.put(Blocks.EMERALD_BLOCK, 0.1);
        probabilityBlock.put(Blocks.EMERALD_ORE, 0.3);
        probabilityBlock.put(Blocks.COAL_ORE, 0.5);
    }

    public void setBlock(final Location location, final Blocks blocks, final boolean applyPhysics) {
        SmoothPrison.getInstance().getBlockChanger().setBlock(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), blocks, applyPhysics);
    }

    public CompletableFuture<Void> setBlockAsync(final Location location, final Blocks blocks, final boolean applyPhysics) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        final WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(SmoothPrison.getInstance(), workloadRunnable, 1, 1);
        workloadRunnable.addWorkload(new BlockSetWorkload(location.getWorld(), location, blocks));
        workloadRunnable.whenComplete(() -> {
            completableFuture.complete(null);
            workloadTask.cancel();
        });
        return completableFuture;
    }

    //Ignore the consumer
    public CompletableFuture<Void> setBlocksChunkSectionAsync(Collection<Location> locations, final Blocks block, final Consumer<Void> onComplete) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        final WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(SmoothPrison.getInstance(), workloadRunnable, 1, 1);
        locations.forEach((location) -> workloadRunnable.addWorkload(new BlockSetChunkSectionWorkload(location.getWorld(), location, block)));
        workloadRunnable.whenComplete(() -> {
            completableFuture.complete(null);
            workloadTask.cancel();
            setPacketBlocks(locations).thenRun(() -> onComplete.accept(null));
        });
        return completableFuture;
    }

    //Ignore the consumer
    public CompletableFuture<Void> setRandomBlocksChunkSectionAsync(Collection<Location> locations, final Consumer<Void> onComplete) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        final WorkloadRunnable workloadRunnable = new WorkloadRunnable();

        final ProbabilitySelector probability = new ProbabilitySelector(probabilityBlock);

        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(SmoothPrison.getInstance(), workloadRunnable, 1, 1);
        locations.forEach((location) -> workloadRunnable.addWorkload(new BlockSetChunkSectionWorkload(location.getWorld(), location, probability.selectBlock())));
        workloadRunnable.whenComplete(() -> {
            completableFuture.complete(null);
            workloadTask.cancel();
            setPacketBlocks(locations).thenRun(() -> onComplete.accept(null));
        });
        return completableFuture;
    }


    public CompletableFuture<Void> setBlocksAsynchronously(World world, Collection<Location> locations,
                                                           Blocks block) {
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();

        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(SmoothPrison.getInstance(), workloadRunnable, 1, 1);
        locations.forEach(location -> workloadRunnable.addWorkload(new BlockSetWorkload(world, location, block)));
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    //Ignore the consumer
    public CompletableFuture<Void> setRandomBlocksAsynchronously(World world, Collection<Location> locations, Consumer<Void> onComplete) {
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();

        final ProbabilitySelector probability = new ProbabilitySelector(probabilityBlock);

        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(SmoothPrison.getInstance(), workloadRunnable, 1, 1);

        locations.forEach(location -> workloadRunnable.addWorkload(new BlockSetWorkload(world, location, probability.selectBlock())));
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
            onComplete.accept(null);
        });
        return workloadFinishFuture;
    }

    public CompletableFuture<Void> setPacketBlocks(final Collection<Location> locations) {
        return CompletableFuture.runAsync(() -> {
            final BlockChangePacket packet = new BlockChangePacket();

            final List<WrapperPlayServerMultiBlockChange> packets = packet.buildPacketList(locations);

            for (WrapperPlayServerMultiBlockChange playServerMultiBlockChange : packets) {
                PacketUtils.broadcastPacket(playServerMultiBlockChange);
            }
        });

    }

    public static BlockHandler getInstance() {
        return instance;
    }

    public Object getOrCreateIBlockData(Blocks blocks) {
        return iBlockDataCache.computeIfAbsent(blocks, block -> ReflectionUtils.getIBlockData(getOrCreateNMSBlock(block)));
    }

    public Object getOrCreateNMSWorld(World world) {
        return nmsWorldCache.computeIfAbsent(world, ReflectionUtils::getNMSWorld);
    }

    public Object getOrCreateBlockPosition(Location location) {
        return blockPositionCache.computeIfAbsent(location, ReflectionUtils::getBlockPosition);
    }

    public Object getOrCreateNMSBlock(Blocks blocks) {
        return nmsBlockCache.computeIfAbsent(blocks, ReflectionUtils::getNMSBlock);
    }

    public void clearCache() {
        iBlockDataCache.clear();
        nmsWorldCache.clear();
        blockPositionCache.clear();
    }
}
