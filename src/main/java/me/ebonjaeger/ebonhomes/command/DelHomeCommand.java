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

import me.ebonjaeger.ebonhomes.Home;
import me.ebonjaeger.ebonhomes.HomesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final HomesManager homesManager;

    public DelHomeCommand(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return options;
        }

        if (!command.getName().equals("delhome")) {
            return options;
        }

        List<Home> homes = this.homesManager.getHomesForPlayer(player.getUniqueId());
        if (homes == null) {
            return options;
        }

        // If the player has started to give an argument
        if (args.length == 1) {
            // If the first argument is empty
            if (args[0].equals("")) {
                // Add all home names as suggestions
                for (Home home : homes) {
                    options.add(home.getName());
                }
            } else {
                // First arg is not empty
                for (Home home : homes) {
                    // Check if what the player has typed so far matches the home's name
                    if (args[0].regionMatches(true, 0, home.getName(), 0, args[0].length())) {
                        options.add(home.getName());
                    }
                }
            }
        }

        return options;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid args! Usage: " + ChatColor.WHITE + "/delhome name");
            return true;
        }

        String name = args[0];

        List<Home> homes = homesManager.getHomesForPlayer(player.getUniqueId());
        if (homes == null || homes.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "You don't have any homes!");
            return true;
        }

        Home found = homesManager.getHome(player.getUniqueId(), name);
        if (found == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find a home named '" + ChatColor.WHITE + name + ChatColor.RED + "'");
            return true;
        }

        homes.remove(found);
        this.homesManager.setPlayerHomes(player.getUniqueId(), homes);
        player.sendMessage(ChatColor.GREEN + "Home deleted successfully!");

        return true;
    }
}
