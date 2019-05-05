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
package nyanguymf.bukkit.globalme;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import nyanguymf.bukkit.globalme.command.MeCommmandExecutor;
import nyanguymf.bukkit.globalme.handlers.ColorParser;
import nyanguymf.bukkit.globalme.handlers.FormatHandler;
import nyanguymf.bukkit.globalme.handlers.RangeHandler;

/** @author NyanGuyMF - Vasiliy Bely */
public final class GlobalMePlugin extends JavaPlugin {
    @Override public void onLoad() {
        File configFile;
        if (!(configFile = new File(super.getDataFolder(), "config.yml")).exists()) {
            super.saveDefaultConfig();
        }

        try {
            super.getConfig().load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            System.err.printf(
                "Unable to load configuration file: %s\n", ex.getLocalizedMessage()
            );
        }

        new ColorParser(super.getConfig()).register();
        new RangeHandler(super.getConfig()).register();
        new FormatHandler(super.getConfig()).register();
    }

    @Override public void onEnable() {
        getCommand("gmereload").setExecutor((sender, cmd, alias, args) -> {
            try {
                super.getConfig().load(new File(super.getDataFolder(), "config.yml"));
            } catch (IOException | InvalidConfigurationException ex) {
                System.err.printf(
                    "Unable to load configuration file: %s\n", ex.getLocalizedMessage()
                );
                return true;
            }

            sender.sendMessage(translateAlternateColorCodes('&', super.getConfig().getString(
                "messages.reloaded", "&3GlobalMe &8» &ePlugin configuration reloaded."
            )));
            return true;
        });

        CommandExecutor meExecutor = new MeCommmandExecutor();

        getCommand("me").setExecutor(meExecutor);
        getCommand("gme").setExecutor(meExecutor);
    }
}
