package me.mastromast50.mastroBanks.utils;

import me.mastromast50.mastroBanks.MastroBanks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ATMManager {
    private final MastroBanks plugin;
    private final Set<Location> atmLocations = new HashSet<>();

    public ATMManager(MastroBanks plugin) {
        this.plugin = plugin;
        loadATMBlocks();
    }

    public void addATMBlock(Block block) {
        atmLocations.add(block.getLocation());
        saveATMBlocks();
        plugin.getLogger().info("ATM aggiunto a " + block.getLocation());
    }

    public void removeATMBlock(Block block) {
        atmLocations.remove(block.getLocation());
        saveATMBlocks();
    }

    public boolean isATMBlock(Block block) {
        return atmLocations.contains(block.getLocation());
    }

    private void saveATMBlocks() {
        List<String> locations = atmLocations.stream()
                .map(loc -> loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ())
                .collect(Collectors.toList());

        plugin.getConfig().set("atm-blocks", locations);
        plugin.saveConfig();
    }

    private void loadATMBlocks() {
        List<String> locations = plugin.getConfig().getStringList("atm-blocks");
        for (String locString : locations) {
            String[] parts = locString.split(";");
            World world = plugin.getServer().getWorld(parts[0]);
            if (world != null) {
                Location loc = new Location(world, Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
                atmLocations.add(loc);
            }
        }
    }
    public void showATMEffect(Location loc) {
        loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                loc.clone().add(0.5, 0.5, 0.5),
                10, 0.3, 0.3, 0.3);
    }

}