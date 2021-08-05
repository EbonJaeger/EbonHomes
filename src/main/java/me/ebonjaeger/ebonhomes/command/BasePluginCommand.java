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

public class BasePluginCommand implements CommandExecutor, TabCompleter {

    private final EbonHomes plugin;

    public BasePluginCommand(EbonHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return options;
        }

        if (!command.getName().equals("ebonhomes")) {
            return options;
        }

        if (args.length == 1) {
            options.add("reload");
        }

        return options;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            String subCommand = args[0];

            if (subCommand.equalsIgnoreCase("reload")) {
                return this.onReloadCommand(sender);
            }
        }

        return false;
    }

    private boolean onReloadCommand(@NotNull CommandSender sender) {
        if (sender.hasPermission("ebonhomes.command.reload")) {
            this.plugin.reloadConfig();

            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        }

        return true;
    }
}
