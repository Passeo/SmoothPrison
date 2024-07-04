package id.passeo.smoothprison.blockhandle.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import id.passeo.smoothprison.SmoothPrison;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class BlockChangePacket {

    private final Map<Vector3i, MultiBlockChange> multiBlockChangeHashMap = new HashMap<>();

    public BlockChangePacket() {
    }

    public void sendChangeBlock(final Collection<Location> locations) {
        final List<WrapperPlayServerMultiBlockChange> fakeBlockPackets = buildPacketList(locations);
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (WrapperPlayServerMultiBlockChange packet : fakeBlockPackets) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
            }
        }
    }

    public List<WrapperPlayServerMultiBlockChange> buildPacketList(final Collection<Location> locations) {
        final List<WrapperPlayServerMultiBlockChange> fakeBlockPackets = new ArrayList<>();
        for (Location location : locations) {
            final Vector3i blockPosition = new Vector3i(location.getChunk().getX(), location.getBlockY() >> 4, location.getChunk().getZ());
            final MultiBlockChange multiBlockChange = getOrCreate(blockPosition);
            final WrapperPlayServerMultiBlockChange.EncodedBlock wrappedBlockState = new WrapperPlayServerMultiBlockChange.EncodedBlock(SpigotConversionUtil.fromBukkitBlockData(location.getBlock().getBlockData()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            multiBlockChange.addBlockDataAtLocation(wrappedBlockState, location);
        }
        multiBlockChangeHashMap.values().forEach(multiBlockChange -> fakeBlockPackets.add(multiBlockChange.build()));
        SmoothPrison.getInstance().getSLF4JLogger().info("Current Thread: {} - {} - {}", Thread.currentThread().getName(), Thread.currentThread().threadId(), Thread.currentThread().getState());
        return fakeBlockPackets;
    }

    private MultiBlockChange getOrCreate(Vector3i position) {
        if(multiBlockChangeHashMap.containsKey(position)) {
            return multiBlockChangeHashMap.get(position);
        } else {
            MultiBlockChange multiBlockChange = new MultiBlockChange();
            multiBlockChangeHashMap.put(position, multiBlockChange);
            return multiBlockChange;
        }
    }

    /**
     * A class to store the data for a single MultiBlockChange packet
     */
    protected static class MultiBlockChange {

        private final ArrayList<Location> locationList;
        private final ArrayList<WrapperPlayServerMultiBlockChange.EncodedBlock> blockDataList;

        /**
         * Constructor for the {@link MultiBlockChange} class
         */
        protected MultiBlockChange() {
            this.locationList = new ArrayList<>();
            this.blockDataList = new ArrayList<>();
        }

        /**
         * Set the section position
         *
         * @param location The location to set
         */
        public void addBlockDataAtLocation(WrapperPlayServerMultiBlockChange.EncodedBlock wrappedBlockData, Location location) {
            blockDataList.add(wrappedBlockData);
            locationList.add(location);
        }

        /**
         * Build the packet
         *
         * @return The built packet
         */
        public WrapperPlayServerMultiBlockChange build() {
            final WrapperPlayServerMultiBlockChange wrapper = new WrapperPlayServerMultiBlockChange(null, null, null);
            final WrapperPlayServerMultiBlockChange.EncodedBlock[] blockDataArray = blockDataList.toArray(new WrapperPlayServerMultiBlockChange.EncodedBlock[0]);
            final Location location = locationList.getFirst();
            wrapper.setChunkPosition(new Vector3i(location.getChunk().getX(), location.getBlockY() >> 4, location.getChunk().getZ()));
            wrapper.setTrustEdges(false);
            wrapper.setBlocks(blockDataArray);

            return wrapper;
        }
    }
}
