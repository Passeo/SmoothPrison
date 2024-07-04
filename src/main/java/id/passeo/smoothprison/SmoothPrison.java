package id.passeo.smoothprison;

import com.github.retrooper.packetevents.PacketEvents;
import id.passeo.smoothprison.blockhandle.BlockHandler;
import id.passeo.smoothprison.blockhandle.Blocks;
import id.passeo.smoothprison.blockhandle.IBlockChanger;
import id.passeo.smoothprison.commands.DebugCommand;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmoothPrison extends JavaPlugin implements Listener {

    private static SmoothPrison instance;
    private IBlockChanger IBlockChanger;


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;

        PacketEvents.getAPI().init();
        new BlockHandler();
        new DebugCommand();
        Bukkit.getPluginManager().registerEvents(this, this);
        final String version = Bukkit.getBukkitVersion();
        if (version.contains("1.20.4")) {
            IBlockChanger = new id.passeo.smoothprison.blockhandle.v1_20_4.BlockChanger();
        }

        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    /**
     * Cancel block break event. And set the breaking block with NMS world setTypeAndData.
     * This is usually use for instant break
     *
     * @param event BlockBreakEvent
     */
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        event.setCancelled(true);
        BlockHandler.getInstance().setBlock(event.getBlock().getLocation(), Blocks.AIR, false);
    }

    public IBlockChanger getBlockChanger() {
        return IBlockChanger;
    }

    public static SmoothPrison getInstance() {
        return instance;
    }
}
