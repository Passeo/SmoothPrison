package id.passeo.smoothprison.blockhandle.workload;

import id.passeo.smoothprison.SmoothPrison;
import id.passeo.smoothprison.blockhandle.Blocks;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockSetWorkload implements Workload{

    private final World world;
    private final Location location;
    private final Blocks block;

    public BlockSetWorkload(World world, Location location, Blocks block) {
        this.world = world;
        this.location = location;
        this.block = block;
    }

    @Override
    public boolean compute() {
        SmoothPrison.getInstance().getBlockChanger().setBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), block, false);
        return true;
    }

}
