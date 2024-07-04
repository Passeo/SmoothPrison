package id.passeo.smoothprison.commands;

import id.passeo.smoothprison.SmoothPrison;
import id.passeo.smoothprison.blockhandle.BlockHandler;
import id.passeo.smoothprison.blockhandle.Blocks;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DebugCommand implements CommandExecutor, TabCompleter {

    private final Map<UUID, Location[]> debugLocations = new HashMap<>();


    public DebugCommand() {
        SmoothPrison.getInstance().getCommand("smoothprisondebug").setExecutor(this);
        SmoothPrison.getInstance().getCommand("smoothprisondebug").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }
        if (strings.length == 0) {
            return false;
        }
        if (strings[0].equalsIgnoreCase("setA")) {
            final Location[] locations = debugLocations.computeIfAbsent(player.getUniqueId(), k -> new Location[2]);
            locations[0] = player.getLocation().getBlock().getLocation();
            player.sendMessage("Location A set: " + "X: " + locations[0].getBlockX() + " Y: " + locations[0].getBlockY() + " Z: " + locations[0].getBlockZ());
            return true;
        }
        if (strings[0].equalsIgnoreCase("setB")) {
            final Location[] locations = debugLocations.computeIfAbsent(player.getUniqueId(), k -> new Location[2]);
            locations[1] = player.getLocation().getBlock().getLocation();
            player.sendMessage("Location B set: " + "X: " + locations[1].getBlockX() + " Y: " + locations[1].getBlockY() + " Z: " + locations[1].getBlockZ());
            return true;
        }
        if (strings[0].equalsIgnoreCase("fill")) {
            final Location locationA = debugLocations.get(player.getUniqueId())[0];
            final Location locationB = debugLocations.get(player.getUniqueId())[1];
            final int size = Math.abs(locationA.getBlockX() - locationB.getBlockX() * (locationA.getBlockY() - locationB.getBlockY()) * (locationA.getBlockZ() - locationB.getBlockZ()));
            final long start = System.currentTimeMillis();
            if (strings[1].equalsIgnoreCase("random")) {
                BlockHandler.getInstance().setRandomBlocksAsynchronously(locationA.getWorld(), getLocation(locationA, locationB), (ignored) -> player.sendMessage("Time Taken: " + (System.currentTimeMillis() - start) + "ms")).thenRun(() -> {
                    player.sendMessage(size + " Block changed in the selected area.");
                    player.sendMessage("Blocks set to " + strings[1] + " in the selected area.");
                });
            } else {
                BlockHandler.getInstance().setBlocksAsynchronously(locationA.getWorld(), getLocation(locationA, locationB), Blocks.valueOf(strings[1]))
                        .thenRun(() -> {
                            player.sendMessage(size + " Block changed in the selected area.");
                            player.sendMessage("Blocks set to " + strings[1] + " in the selected area.");
                        });
            }
            return true;
        }
        if (strings[0].equalsIgnoreCase("fillcs")) {
            final Location locationA = debugLocations.get(player.getUniqueId())[0];
            final Location locationB = debugLocations.get(player.getUniqueId())[1];
            if (locationA == null || locationB == null) {
                player.sendMessage("Please set location A and B first");
                return false;
            }
            final int size = Math.abs(locationA.getBlockX() - locationB.getBlockX()) * (locationA.getBlockY() - locationB.getBlockY()) * (locationA.getBlockZ() - locationB.getBlockZ());
            final long start = System.currentTimeMillis();
            if (strings[1].equalsIgnoreCase("random")) {
                BlockHandler.getInstance().setRandomBlocksChunkSectionAsync(getLocation(locationA, locationB), (ignored) -> player.sendMessage("Blocks set to " + strings[1] + " in the selected area."))
                        .thenRun(() -> player.sendMessage("Time Taken: " + (System.currentTimeMillis() - start) + "ms"))
                        .thenRun(() -> player.sendMessage(size + " Block changed in the selected area."));
            } else {
                BlockHandler.getInstance().setBlocksChunkSectionAsync(getLocation(locationA, locationB), Blocks.valueOf(strings[1]), (ignored) -> player.sendMessage("Blocks set to " + strings[1] + " in the selected area."));
            }
        }
        return false;
    }

    private List<Location> getLocation(final Location locationA, final Location locationB) {
        final BoundingBox bb = new BoundingBox(locationA.getX(), locationA.getY(), locationA.getZ(), locationB.getX(), locationB.getY(), locationB.getZ());
        final List<Location> locations = new ArrayList<>();
        for (double x = bb.getMinX(); x <= bb.getMaxX(); x++) {
            for (double y = bb.getMinY(); y <= bb.getMaxY(); y++) {
                for (double z = bb.getMinZ(); z <= bb.getMaxZ(); z++) {
                    locations.add(new Location(locationA.getWorld(), x, y, z));
                }
            }
        }
        return locations;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final List<String> collections = new ArrayList<>();
        final List<String> completions = List.of("setA", "setB", "fill", "fillcs");
        if (strings.length == 2) {
            final List<String> strings1 = Arrays.stream(Blocks.values()).collect(ArrayList::new, (list, blocks) -> list.add(blocks.name()), ArrayList::addAll);
            StringUtil.copyPartialMatches(strings[1], strings1, collections);
            return collections;
        }
        StringUtil.copyPartialMatches(strings[0], completions, collections);
        return collections;
    }
}
