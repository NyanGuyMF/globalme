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
package nyanguymf.bukkit.globalme.command;

import static java.util.stream.Collectors.joining;
import static nyanguymf.bukkit.globalme.handlers.RangeHandler.getNearbyPlayers;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import nyanguymf.bukkit.globalme.event.GmeCommandEventListener;
import nyanguymf.bukkit.globalme.event.GmeMessageEvent;
import nyanguymf.bukkit.globalme.event.GmeMessageEvent.COMMAND_TYPE;
import nyanguymf.bukkit.globalme.handlers.RangeHandler;

/** @author NyanGuyMF - Vasiliy Bely */
public final class MeCommmandExecutor implements CommandExecutor {
    @Override public boolean onCommand(
        final CommandSender sender, final Command command,
        final String alias, final String[] args
    ) {
        if (args.length == 0)
            return false;

        String message = Arrays.stream(args).parallel().collect(joining(" "));
        COMMAND_TYPE type = alias.equalsIgnoreCase("me")
                ? COMMAND_TYPE.ME
                : COMMAND_TYPE.GME;

        GmeMessageEvent event = new GmeMessageEvent(sender, message, type);

        for (GmeCommandEventListener listener : event.getListeners()) {
            if (event.isCancelled()) {
                sender.sendMessage(translateAlternateColorCodes('&', event.getCancelReason()));
                return true;
            }

            listener.onMessage(event);
        }

        String format = event.getFormat().replace("{player-name}", sender.getName());
        String formatedMessage = translateAlternateColorCodes('&', format)
                .replace("{message}", event.getMessage());

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(formatedMessage);
            for (Player player : getOnlinePlayers()) {
                player.sendMessage(formatedMessage);
            }
            return true;
        }

        if (event.getRange() > RangeHandler.WORLD_RANGE) {
            sender.sendMessage(formatedMessage);
            for (Player player : getNearbyPlayers((Player) sender, event.getRange())) {
                player.sendMessage(formatedMessage);
            }
        } else if (event.getRange() == RangeHandler.WORLD_RANGE) {
            for (Player player : ((Player) sender).getWorld().getPlayers()) {
                player.sendMessage(formatedMessage);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(formatedMessage);
            }
        }

        return true;
    }
}
