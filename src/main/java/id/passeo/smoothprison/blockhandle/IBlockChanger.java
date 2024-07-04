package id.passeo.smoothprison.blockhandle;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;

public interface IBlockChanger {

    void setBlock(World world, int x, int y, int z, Blocks block, boolean applyPhysics);
    void setBlocksChunkSection(World world, Collection<Location> locations, Blocks block, boolean applyPhysics);
    void setBlockChunkSection(World world, Location locations, Blocks block, boolean applyPhysics);
}
