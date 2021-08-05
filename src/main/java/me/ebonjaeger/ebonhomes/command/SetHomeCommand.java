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

package me.ebonjaeger.ebonhomes.command;

import me.ebonjaeger.ebonhomes.EbonHomes;
import me.ebonjaeger.ebonhomes.Home;
import me.ebonjaeger.ebonhomes.HomesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor {

    private final EbonHomes plugin;
    private final HomesManager homesManager;

    public SetHomeCommand(EbonHomes plugin, HomesManager homesManager) {
        this.plugin = plugin;
        this.homesManager = homesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid args! Usage: " + ChatColor.WHITE + "/sethome name");
            return true;
        }

        // Check if the player is at their home limit
        int limit = this.plugin.getHomeLimit(player);
        int count = this.homesManager.getHomeCount(player.getUniqueId());
        if (limit != -1 && count >= limit) {
            player.sendMessage(ChatColor.RED + "You are already at the home limit!");
            return true;
        }

        String name = args[0];

        // Check for existing home with the same name
        Home existing = this.homesManager.getHome(player.getUniqueId(), name);
        if (existing != null) {
            player.sendMessage(ChatColor.RED + "You already have a home with that name");
            return true;
        }

        Home home = new Home(name, player.getLocation());
        this.homesManager.addHomeForPlayer(player.getUniqueId(), home);
        player.sendMessage(ChatColor.GREEN + "Home set successfully! You have " + ChatColor.WHITE + ((limit == -1) ? "unlimited" : (limit - count - 1)) + ChatColor.GREEN + " homes remaining.");

        return true;
    }
}
