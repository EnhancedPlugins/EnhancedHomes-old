package com.enhancedplugins.enhancedhomes.managers;

import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The HomeManager class manages the homes of players.
 * It provides methods to add, update, remove, and retrieve homes.
 * It also handles the loading and saving of homes to and from disk.
 */
public class HomeManager {
    private final JavaPlugin plugin;
    private final Map<UUID, List<Home>> homesMap = new HashMap<>();

    /**
     * Constructor for the HomeManager class.
     *
     * @param plugin The JavaPlugin instance.
     */
    public HomeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadHomes();
    }

    /**
     * Retrieves a specific home of a player.
     *
     * @param player The player.
     * @param homeName The name of the home.
     * @return The home, or null if the home does not exist.
     */
    public Home getHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();
        List<Home> playerHomes = homesMap.get(playerId);
        if (playerHomes == null) {
            playerHomes = new ArrayList<>();
            homesMap.put(playerId, playerHomes);
            saveHomes(player);
        }
        return playerHomes.stream()
                .filter(home -> home.getName().equals(homeName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all homes of a player.
     *
     * @param player The player.
     * @return A list of the player's homes.
     */
    public List<Home> getHomes(Player player) {
        return homesMap.getOrDefault(player.getUniqueId(), Collections.emptyList());
    }

    /**
     * Adds a home for a player.
     *
     * @param player The player.
     * @param home The home to add.
     */
    public void addHome(Player player, Home home) {
        homesMap.get(player.getUniqueId()).add(home);
        saveHomes(player);
    }

    /**
     * Updates a specific home of a player.
     *
     * @param player The player.
     * @param homeName The name of the home to update.
     * @param newWorldName The new world name of the home.
     * @param newX The new X coordinate of the home.
     * @param newY The new Y coordinate of the home.
     * @param newZ The new Z coordinate of the home.
     */
    public void updateHome(Player player, String homeName, String newWorldName, double newX, double newY, double newZ) {
        List<Home> playerHomes = homesMap.get(player.getUniqueId());
        for (Home home : playerHomes) {
            if (home.getName().equals(homeName)) {
                home.setWorldName(newWorldName);
                home.setX(newX);
                home.setY(newY);
                home.setZ(newZ);
                break;
            }
        }
        saveHomes(player);
    }

    /**
     * Removes a specific home of a player.
     *
     * @param player The player.
     * @param homeName The name of the home to remove.
     */
    public void removeHome(Player player, String homeName) {
        homesMap.get(player.getUniqueId()).removeIf(home -> home.getName().equals(homeName));
        saveHomes(player);
    }

    /**
     * Loads all homes from disk.
     * This method is called when the HomeManager is instantiated.
     */
    private void loadHomes() {
        File homesDir = new File(plugin.getDataFolder(), "homes");
        if (!homesDir.exists()) {
            homesDir.mkdirs();
        }
        for (File file : Objects.requireNonNull(homesDir.listFiles())) {
            UUID playerId = UUID.fromString(file.getName().replace(".yml", ""));
            YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(file);
            List<Home> homes = new ArrayList<>();
            if(yamlFile.getConfigurationSection("homes") != null) {
                yamlFile.getConfigurationSection("homes").getKeys(false).forEach(homeName -> {
                    String world = yamlFile.getString("homes." + homeName + ".world");
                    double x = yamlFile.getDouble("homes." + homeName + ".x");
                    double y = yamlFile.getDouble("homes." + homeName + ".y");
                    double z = yamlFile.getDouble("homes." + homeName + ".z");
                    homes.add(new Home(homeName, world, x, y, z));
                });
                homesMap.put(playerId, homes);
            }
        }
    }

    /**
     * Saves all homes of a player to disk.
     *
     * @param player The player whose homes to save.
     */
    private void saveHomes(Player player) {
        File homesFile = new File(plugin.getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        YamlConfiguration yamlFile = new YamlConfiguration();
        for (Home home : homesMap.get(player.getUniqueId())) {
            String path = "homes." + home.getName();
            yamlFile.set(path + ".world", home.getWorldName());
            yamlFile.set(path + ".x", home.getX());
            yamlFile.set(path + ".y", home.getY());
            yamlFile.set(path + ".z", home.getZ());
        }
        try {
            yamlFile.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes for player " + player.getName());
        }
    }

    public void reloadHomes() {
        homesMap.clear();
        loadHomes();
    }
}