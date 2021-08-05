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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListHomesCommand implements CommandExecutor {

    private final HomesManager homesManager;

    public ListHomesCommand(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player");
            return true;
        }

        List<Home> homes = this.homesManager.getHomesForPlayer(player.getUniqueId());
        if (homes == null || homes.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "You don't have any homes!");
            return true;
        }

        TextComponent.Builder component = Component.text()
                .content("Your homes: ")
                .color(NamedTextColor.GRAY);
        for (Home home : homes) {
            component.append(home.toTextComponent()).append(Component.text(" "));
        }

        player.sendMessage(component.build());

        return true;
    }
}
