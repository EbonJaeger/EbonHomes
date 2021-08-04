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

import me.ebonjaeger.ebonhomes.command.DelHomeCommand;
import me.ebonjaeger.ebonhomes.command.HomeCommand;
import me.ebonjaeger.ebonhomes.command.SetHomeCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class EbonHomes extends JavaPlugin {

    private Path playerDataPath;

    private HomesManager homesManager;

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

        ConfigurationSerialization.registerClass(Home.class);

        this.homesManager = new HomesManager(this);

        getCommand("delhome").setExecutor(new DelHomeCommand(homesManager));
        getCommand("home").setExecutor(new HomeCommand(homesManager));
        getCommand("sethome").setExecutor(new SetHomeCommand(homesManager));
        getServer().getPluginManager().registerEvents(new PlayerListener(homesManager), this);
    }

    @Override
    public void onDisable() {
        homesManager.saveAllHomes();
    }

    public Path getPlayerDataPath() {
        return this.playerDataPath;
    }
}
