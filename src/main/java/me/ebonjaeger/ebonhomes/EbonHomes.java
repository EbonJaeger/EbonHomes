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

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import me.ebonjaeger.ebonhomes.command.*;
import me.ebonjaeger.ebonhomes.config.HomesSettings;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EbonHomes extends JavaPlugin {

    private Path playerDataPath;

    private HomesManager homesManager;
    private SettingsManager settingsManager;

    private Map<String, Integer> permissionMap;

    @Override
    public void onEnable() {
        // Create the player data directory
        this.playerDataPath = Paths.get(getDataFolder().getPath(), "playerdata");
        if (!Files.exists(this.playerDataPath)) {
            try {
                Files.createDirectories(this.playerDataPath);
            } catch (IOException e) {
                getLogger().severe("Error creating player data directory: " + e.getMessage());
                getPluginLoader().disablePlugin(this);
                return;
            }
        }

        // Save the config file
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!Files.exists(configFile.toPath())) {
            this.saveResource("config.yml", false);
        }

        ConfigurationSerialization.registerClass(Home.class);

        this.settingsManager = SettingsManagerBuilder
                .withYamlFile(configFile)
                .configurationData(HomesSettings.class)
                .useDefaultMigrationService()
                .create();

        this.permissionMap = new HashMap<>();
        this.makePermissionMap();

        this.homesManager = new HomesManager(this);

        getCommand("ebonhomes").setExecutor(new BasePluginCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(homesManager));
        getCommand("home").setExecutor(new HomeCommand(homesManager));
        getCommand("listhomes").setExecutor(new ListHomesCommand(homesManager));
        getCommand("sethome").setExecutor(new SetHomeCommand(this, homesManager));
        getServer().getPluginManager().registerEvents(new PlayerListener(homesManager), this);
    }

    @Override
    public void onDisable() {
        homesManager.saveAllHomes();
    }

    /**
     * Calculate the home limit for a player.
     *
     * This checks for any configured permissions, setting
     * the limit to the highest number that applies.
     *
     * If none of them apply, then use the default configured
     * home limit.
     *
     * @param player The player to check
     * @return The number of homes the player can have, with -1 meaning unlimited
     */
    public int getHomeLimit(@NotNull Player player) {
        int limit = 0;

        // Look through all the configured permissions
        for (String permission : this.permissionMap.keySet()) {
            // If the player has permission...
            if (player.hasPermission(permission)) {
                int permLimit = this.permissionMap.get(permission);
                // If the permission's limit is higher than any previous value
                if (permLimit > limit) {
                    // Set the limit to the higher limit
                    limit = permLimit;
                }
            }
        }

        // Fallback to the default configured home limit
        if (limit == 0) {
            limit = this.settingsManager.getProperty(HomesSettings.HOME_LIMIT);
        }

        return limit;
    }

    public @NotNull Path getPlayerDataPath() {
        return this.playerDataPath;
    }

    private void makePermissionMap() {
        this.permissionMap.clear();

        List<String> raw = settingsManager.getProperty(HomesSettings.LIMIT_PERMISSIONS);
        for (String line : raw) {
            String[] parts = line.split(":");
            if (parts.length != 2) {
                this.getLogger().severe("Malformed limit permission: '" + line + "'");
                continue;
            }

            int limit;
            try {
                limit = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                this.getLogger().severe("Malformed limit permission: '" + parts[1] + "' is not a number");
                continue;
            }

            this.permissionMap.put(parts[0], limit);
        }
    }

    public void reloadConfig() {
        this.settingsManager.reload();
        this.makePermissionMap();
    }
}
