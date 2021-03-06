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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Home implements ConfigurationSerializable {

    private final String name;
    private Location location;

    public Home(@NotNull String name, @NotNull Location location) {
        this.name = name;
        this.location = location;
    }

    @SuppressWarnings("unchecked")
    public Home(@NotNull Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.location = Location.deserialize((Map<String, Object>) map.get("location"));
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", this.name);
        ret.put("location", location.serialize());
        return ret;
    }

    /**
     * Creates a new {@link TextComponent} with a hover event to
     * display information about this home.
     *
     * @return A TextComponent consisting of the name and hover event
     */
    public @NotNull TextComponent toTextComponent() {
        return Component.text(this.name, NamedTextColor.WHITE)
                .hoverEvent(HoverEvent.showText(Component.text(
                        "World: " + this.location.getWorld().getName() +
                        "\nx: " + this.location.getX() +
                        "\ny: " + this.location.getY() +
                        "\nz: " + this.location.getZ())));
    }
}
