package id.passeo.smoothprison.blockhandle.workload;

import id.passeo.smoothprison.SmoothPrison;
import id.passeo.smoothprison.blockhandle.Blocks;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockSetChunkSectionWorkload implements Workload{

    private final World world;
    private final Location location;
    private final Blocks block;

    public BlockSetChunkSectionWorkload(World world, Location location, Blocks block){
        this.world = world;
        this.location = location;
        this.block = block;
    }

    @Override
    public boolean compute() {
        SmoothPrison.getInstance().getBlockChanger().setBlockChunkSection(world, location, block, false);
        return true;
    }
}
