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

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import nyanguymf.bukkit.globalme.event.GmeMessageEvent;
import nyanguymf.bukkit.globalme.event.GmeCommandEventListener;

/** @author NyanGuyMF - Vasiliy Bely */
public final class FormatHandler implements GmeCommandEventListener {
    private FileConfiguration config;

    public FormatHandler(final FileConfiguration config) {
        this.config = config;
    }

    public GmeCommandEventListener register() {
        GmeMessageEvent.registerListener(this);
        return this;
    }

    @Override public void onMessage(final GmeMessageEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender) {
            event.setFormat(config.getString(
                "format.broadcast",
                "* {player-name} {message}"
            ));
        } else {
            event.setFormat(config.getString(
                "format." + event.getType().toString().toLowerCase(),
                "* {player-name} {message}"
            ));
        }
    }
}
