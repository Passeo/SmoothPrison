package id.passeo.smoothprison.blockhandle;

import org.bukkit.Bukkit;

public enum Blocks {
    AIR("air"), STONE("stone"), GRASS_BLOCK("grass_block"), DIRT("dirt"), COBBLESTONE("cobblestone"),
    COAL_BLOCK("coal_block"), IRON_BLOCK("iron_block"), GOLD_BLOCK("gold_block"), DIAMOND_BLOCK("diamond_block"),
    EMERALD_BLOCK("emerald_block"), LAPIS_BLOCK("lapis_block"), REDSTONE_BLOCK("redstone_block"),
    NETHERITE_BLOCK("netherite_block"), QUARTZ_BLOCK("quartz_block"), OBSIDIAN("obsidian"),
    COAL_ORE("coal_ore"), IRON_ORE("iron_ore"), GOLD_ORE("gold_ore"), DIAMOND_ORE("diamond_ore"),
    EMERALD_ORE("emerald_ore"), LAPIS_ORE("lapis_ore"), REDSTONE_ORE("redstone_ore"), ANCIENT_DEBRIS("ancient_debris");


    private final String name;

    Blocks(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        final String version = Bukkit.getVersion();
        if (version.contains("1.20.4")) {
            switch (name) {
                case "air":
                    return "a";
                case "stone":
                    return "b";
                case "grass_block":
                    return "i";
                case "dirt":
                    return "j";
                case "cobblestone":
                    return "m";
                case "coal_block":
                    return "iB";
                case "iron_block":
                    return "ci";
                case "gold_block":
                    return "ch";
                case "diamond_block":
                    return "cz";
                case "emerald_block":
                    return "fJ";
                case "lapis_block":
                    return "aT";
                case "redstone_block":
                    return "ha";
                case "netherite_block":
                    return "pi";
                case "quartz_block":
                    return "hd";
                case "obsidian":
                    return "co";
                case "coal_ore":
                    return "R";
                case "iron_ore":
                    return "P";
                case "gold_ore":
                    return "N";
                case "diamond_ore":
                    return "cx";
                case "emerald_ore":
                    return "fE";
                case "lapis_ore":
                    return "aR";
                case "redstone_ore":
                    return "dI";
                case "ancient_debris":
                    return "pj";
                default:
                    return "a";
            }
        }
        return "a";
    }
}
