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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final HomesManager homesManager;

    public PlayerListener(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.homesManager.loadHomesForPlayer(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.homesManager.saveHomesForPlayer(player.getUniqueId());
        this.homesManager.unloadHomesForPlayer(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        this.homesManager.saveHomesForPlayer(player.getUniqueId());
        this.homesManager.unloadHomesForPlayer(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        Location location = player.getBedSpawnLocation();

        // This should never happen if the player successfully slept
        // in the bed.
        if (location == null) {
            return;
        }

        // Check for an existing bed home location
        Home existing = this.homesManager.getHome(player.getUniqueId(), "bed");
        if (existing != null) {
            // An existing home exists. Update it instead of making a new one
            existing.setLocation(location);
            return;
        }

        Home home = new Home("bed", location);
        this.homesManager.addHomeForPlayer(player.getUniqueId(), home);
    }
}
