package id.passeo.smoothprison.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.Bukkit;

public class PacketUtils {

    public static void broadcastPacket(final PacketWrapper<?> packet){
        Bukkit.getOnlinePlayers().forEach(player -> PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet));
    }

}
