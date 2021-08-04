// EbonHomes is a simple home management plugin for Minecraft servers.
// Copyright (C) 2021  Evan Maddock (maddock.evan@vivaldi.net)
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

package me.ebonjaeger.ebonhomes;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HomesManager {

    private final Map<UUID, List<Home>> loadedHomes;
    private final EbonHomes plugin;

    public HomesManager(EbonHomes plugin) {
        this.loadedHomes = new HashMap<>();
        this.plugin = plugin;
    }

    /**
     * Add a home to a player's list of homes. This will
     * replace any home that already has the same name.
     *
     * @param uuid The player's unique ID
     * @param home The {@link Home} to add
     */
    public void addHomeForPlayer(UUID uuid, Home home) {
        List<Home> currentHomes = getHomesForPlayer(uuid);
        if (currentHomes == null) {
            // This player doesn't have any homes yet
            currentHomes = new ArrayList<>();
        }

        currentHomes.add(home);
        this.loadedHomes.put(uuid, currentHomes);
    }

    /**
     * Gets all homes for a player, if any are loaded.
     *
     * @param uuid The player's unique ID
     * @return A Set of homes for a player, or NULL
     */
    public @Nullable List<Home> getHomesForPlayer(UUID uuid) {
        return this.loadedHomes.get(uuid);
    }

    /**
     * Gets the home of a player with the given name.
     *
     * @param uuid The player's unique ID
     * @param name The name of the home
     * @return The found home or {@code null}
     */
    public @Nullable Home getHome(UUID uuid, @NotNull String name) {
        List<Home> homes = getHomesForPlayer(uuid);
        if (homes == null) {
            return null;
        }

        Home found = null;
        for (Home home : homes) {
            if (home.getName().equals(name)) {
                found = home;
                break;
            }
        }

        return found;
    }

    /**
     * Load any homes for a player from disk.
     *
     * @param uuid The player's unique ID
     */
    public void loadHomesForPlayer(UUID uuid) {
        Path playerPath = Paths.get(plugin.getPlayerDataPath().toString(), uuid.toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerPath.toFile());

        List<?> data = config.getList("homes");
        if (data == null || data.isEmpty()) {
            return;
        }

        // Make sure our data is what we expect
        List<Home> homes = new ArrayList<>();
        for (Object o : data) {
            if (o instanceof Home) {
                homes.add((Home) o);
            }
        }

        this.loadedHomes.put(uuid, homes);
    }

    /**
     * Save the homes of all players to disk and clear all entries.
     */
    public void saveAllHomes() {
        for (UUID key : this.loadedHomes.keySet()) {
            saveHomesForPlayer(key);
        }

        this.loadedHomes.clear();
    }

    /**
     * Save any homes for the player to disk.
     *
     * @param uuid The player's unique ID
     */
    public void saveHomesForPlayer(UUID uuid) {
        List<Home> currentHomes = getHomesForPlayer(uuid);
        if (currentHomes == null) {
            // This player doesn't have any homes yet
            return;
        }

        Path playerPath = Paths.get(plugin.getPlayerDataPath().toString(), uuid.toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerPath.toFile());
        config.set("homes", currentHomes);

        try {
            config.save(playerPath.toFile());
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving config for player '" + uuid + "': " + e.getMessage());
        }
    }

    /**
     * Set the home locations for a player. This will overwrite any
     * existing homes.
     *
     * @param uuid The player's unique ID
     * @param homes The homes that the player has
     */
    public void setPlayerHomes(UUID uuid, @NotNull List<Home> homes) {
        this.loadedHomes.put(uuid, homes);
    }

    /**
     * Removes all homes for the player from memory.
     *
     * @param uuid The player's unique ID
     */
    public void unloadHomesForPlayer(UUID uuid) {
        this.loadedHomes.remove(uuid);
    }
}
