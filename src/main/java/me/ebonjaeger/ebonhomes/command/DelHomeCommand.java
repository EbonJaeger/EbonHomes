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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelHomeCommand implements CommandExecutor {

    private final HomesManager homesManager;

    public DelHomeCommand(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid args! Usage: " + ChatColor.WHITE + "/delhome name");
            return true;
        }

        String name = args[0];

        List<Home> homes = homesManager.getHomesForPlayer(player.getUniqueId());
        if (homes == null) {
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
