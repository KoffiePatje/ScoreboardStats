package com.github.games647.scoreboardstats;

import java.io.File;

import org.bukkit.plugin.Plugin;

/**
 * This custom class fixes the issue that the curse api doesn't update so fast.
 *
 * Maybe this class execute the request with the old-style system. (files.rss)
 * Which is every time up to date.
 *
 * @see Updater
 */
public class UpdaterFix extends Updater {

    /**
     * Creates a new Updater
     *
     * @param plugin The plugin that is checking for an update.
     * @param file The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     */
    public UpdaterFix(Plugin plugin, File file) {
        super(plugin, file, 55148, true);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return Version.compare(localVersion, remoteVersion) < 0;
    }
}
