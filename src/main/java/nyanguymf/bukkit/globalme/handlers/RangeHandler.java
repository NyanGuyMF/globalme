/*
 * This file is part of GlobalMe Bukkit plug-in.
 *
 * GlobalMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GlobalMe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GlobalMe. If not, see <https://www.gnu.org/licenses/>.
 */
package nyanguymf.bukkit.globalme.handlers;

import static org.bukkit.Bukkit.getOnlinePlayers;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import nyanguymf.bukkit.globalme.event.GmeCommandEventListener;
import nyanguymf.bukkit.globalme.event.GmeMessageEvent;

/** @author NyanGuyMF - Vasiliy Bely */
public final class RangeHandler implements GmeCommandEventListener {
    public static final long WORLD_RANGE = -1;
    public static final long SERVER_RANGE = -2;

    private FileConfiguration config;

    public RangeHandler(final FileConfiguration config) {
        this.config = config;
    }

    public GmeCommandEventListener register() {
        GmeMessageEvent.registerListener(this);
        return this;
    }

    @Override public void onMessage(final GmeMessageEvent event) {
        switch (event.getType()) {
        case GME:
            event.setRange(config.getLong("range.global-me", RangeHandler.SERVER_RANGE));
            break;
        case ME:
            event.setRange(config.getLong("range.local-me", RangeHandler.WORLD_RANGE));
            break;
        }

        if (event.getSender() instanceof ConsoleCommandSender) {
            if (!config.getBoolean("cancel.if-console", false)) {
                event.setRange(RangeHandler.SERVER_RANGE);
            } else {
                event.setCancelReason(config.getString(
                    "messages.console-is-cancelled",
                    "&cConsole cannot send messages."
                ));
                event.setCancelled(true);
            }
            return;
        }

        event.setCancelled(isAlone((Player) event.getSender(), event.getRange()));

        if (event.isCancelled()) {
            if (event.getRange() > RangeHandler.WORLD_RANGE) {
                event.setCancelReason(config.getString(
                    "messages.forever-alone-range",
                    "&eMessage wasn't delivered: &cthere are no nearby players."
                ));
            } else if (event.getRange() == RangeHandler.WORLD_RANGE) {
                event.setCancelReason(config.getString(
                    "messages.forever-alone-world",
                    "&eMessage wasn't delivered: &cyou're alone in this world."
                ));
            } else {
                event.setCancelReason(config.getString(
                    "messages.forever-alone-server",
                    "&eMessage wasn't delivered:&c you're alone on this server."
                ));
            }
        }
    }

    private boolean isAlone(final Player player, final long range) {
        boolean isAlone = false;

        if (range > RangeHandler.WORLD_RANGE) {
            isAlone = config.getBoolean("cancel.no-nearby", true)
                    && (getNearbyPlayers(player, range).size() == 0);
        } else if (range == RangeHandler.WORLD_RANGE) {
            isAlone = config.getBoolean("config.if-alone-in-world", true)
                    && (player.getWorld().getPlayers().size() <= 1);
        } else {
            isAlone = config.getBoolean("config.if-alone-on-server", true)
                    && (Bukkit.getOnlinePlayers().size() <= 1);
        }

        return isAlone;
    }

    public static Collection<? extends Player> getNearbyPlayers(final Player player, final long range) {
        if (range <= RangeHandler.SERVER_RANGE)
            return getOnlinePlayers();

        return getOnlinePlayers().parallelStream()
            .filter(onlinePlayer -> {
                if (!player.getWorld().getName().equals(onlinePlayer.getWorld().getName()))
                    return false;
                return onlinePlayer.getLocation().distance(player.getLocation()) <= range
                    && !player.getName().equals(onlinePlayer.getName());
            })
            .collect(Collectors.toList());
    }
}
