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

package me.ebonjaeger.ebonhomes.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class HomesSettings implements SettingsHolder {

    // Prevent instantiation
    private HomesSettings() {}

    @Comment({"The maximum number of homes a player can have.",
            "A value of -1 means unlimited homes",
            "This can be overridden via permissions."})
    public static final Property<Integer> HOME_LIMIT =
            newProperty("homes.limit", 5);

    @Comment({"List of permission nodes to set the number of homes a player can have.",
            "The format is: example.permission.node:5",
            "where 5 is the number of homes that permission gives."})
    public static final Property<List<String>> LIMIT_PERMISSIONS =
            newListProperty("homes.limit-permissions", "");
}
