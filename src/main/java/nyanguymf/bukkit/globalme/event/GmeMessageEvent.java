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
package nyanguymf.bukkit.globalme.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

/** @author NyanGuyMF - Vasiliy Bely */
public final class GmeMessageEvent implements Cancellable {
    public enum COMMAND_TYPE {
        GME, ME;
    }

    private static volatile List<GmeCommandEventListener> listeners = new ArrayList<>();

    private boolean isCancelled;

    private COMMAND_TYPE type;

    private String message;

    private String format;

    private String cancelReason;

    private CommandSender sender;

    private long range;

    public GmeMessageEvent(
        final CommandSender sender, final String message,
        final COMMAND_TYPE type
    ) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        isCancelled = false;
    }

    public List<GmeCommandEventListener> getListeners() {
        return GmeMessageEvent.listeners;
    }

    public static synchronized boolean registerListener(final GmeCommandEventListener listener) {
        return GmeMessageEvent.listeners.add(listener);
    }

    public boolean removeListener(final GmeCommandEventListener listener) {
        return GmeMessageEvent.listeners.remove(listener);
    }

    @Override public boolean isCancelled() {
        return isCancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        isCancelled = cancel;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public COMMAND_TYPE getType() {
        return type;
    }

    public long getRange() {
        return range;
    }

    public void setRange(final long range) {
        this.range = range;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(final String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }
}
