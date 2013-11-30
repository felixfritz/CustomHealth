/*
 * Updater for Bukkit.
 *
 * This class provides the means to safely and easily update a plugin, or check to see if it is updated using dev.bukkit.org
 */

package at.felixfritz.customhealth.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * This file has been modified and shrunk down for personal usage.
 * 
 * @author Gravity
 * @version 2.0
 */

public class Updater {

    private Plugin plugin;
    private String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;
    
    private URL url; // Connecting to RSS
    private Thread thread; // Updater thread

    private int id = -1; // Project's Curse ID
    private String apiKey = null; // BukkitDev ServerMods API key
    private static final String TITLE_VALUE = "name"; // Gets remote file's title
    private static final String LINK_VALUE = "downloadUrl"; // Gets remote file's download link
    private static final String TYPE_VALUE = "releaseType"; // Gets remote file's release type
    private static final String VERSION_VALUE = "gameVersion"; // Gets remote file's build version
    private static final String QUERY = "/servermods/files?projectIds="; // Path to GET
    private static final String HOST = "https://api.curseforge.com"; // Slugs will be appended to this to get to the project's RSS feed

    private Updater.UpdateResult result = Updater.UpdateResult.SUCCESS; // Used for determining the outcome of the update process
    
    private YamlConfiguration config; // Config file

    /**
     * Gives the dev the result of the update process. Can be obtained by called getResult().
     */
    public enum UpdateResult {
        /**
         * The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.
         */
        SUCCESS,
        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE,
        /**
         * Updater is disabled
         */
        DISABLED,
        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO,
        /**
         * The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADID,
        /**
         * The server administrator has improperly configured their API key in the configuration
         */
        FAIL_APIKEY,
        /**
         * The updater found an update, but because of the UpdateType being set to NO_DOWNLOAD, it wasn't downloaded.
         */
        UPDATE_AVAILABLE
    }

    /**
     * Initialize the updater
     *
     * @param plugin   The plugin that is checking for an update.
     * @param id       The dev.bukkit.org id of the project
     * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     * @param type     Specify the type of update this will be. See {@link UpdateType}
     * @param announce True if the program should announce the progress of new updates in console
     */
    public Updater(Plugin plugin, int id, File file, boolean announce) {
        this.plugin = plugin;
        this.id = id;
        
        final File pluginFile = plugin.getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "Updater");
        final File updaterConfigFile = new File(updaterFile, "config.yml");
        
        if (!updaterFile.exists()) {
            updaterFile.mkdir();
        }
        if (!updaterConfigFile.exists()) {
            try {
                updaterConfigFile.createNewFile();
            } catch (final IOException e) {
                plugin.getLogger().severe("The updater could not create a configuration in " + updaterFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(updaterConfigFile);

        this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )" + '\n'
                + "If you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below." + '\n'
                + "Some updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        this.config.addDefault("api-key", "PUT_API_KEY_HERE");
        this.config.addDefault("disable", false);

        if (this.config.get("api-key", null) == null) {
            this.config.options().copyDefaults(true);
            try {
                this.config.save(updaterConfigFile);
            } catch (final IOException e) {
                plugin.getLogger().severe("The updater could not save the configuration in " + updaterFile.getAbsolutePath());
                e.printStackTrace();
            }
        }

        if (this.config.getBoolean("disable")) {
            this.result = UpdateResult.DISABLED;
            return;
        }

        String key = this.config.getString("api-key");
        if (key.equalsIgnoreCase("PUT_API_KEY_HERE") || key.equals("")) {
            key = null;
        }

        this.apiKey = key;

        try {
            this.url = new URL(Updater.HOST + Updater.QUERY + id);
        } catch (final MalformedURLException e) {
            plugin.getLogger().severe("The project ID provided for updating, " + id + " is invalid.");
            this.result = UpdateResult.FAIL_BADID;
            e.printStackTrace();
        }
        
        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }

    /**
     * Get the result of the update process.
     */
    public Updater.UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    /**
     * Get the latest version's release type (release, beta, or alpha).
     */
    public String getLatestType() {
        this.waitForThread();
        return this.versionType;
    }

    /**
     * Get the latest version's game version.
     */
    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }

    /**
     * Get the latest version's name.
     */
    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    /**
     * Get the latest version's file link.
     */
    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    /**
     * As the result of Updater output depends on the thread's completion, it is necessary to wait for the thread to finish
     * before allowing anyone to check the result.
     */
    private void waitForThread() {
        if ((this.thread != null) && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean read() {
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);
            
            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", "Updater (by Gravity)");
            
            conn.setDoOutput(true);
            
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            
            final JSONArray array = (JSONArray) JSONValue.parse(response);
            
            if (array.size() == 0) {
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }
            
            this.versionName = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TITLE_VALUE);
            this.versionLink = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.LINK_VALUE);
            this.versionType = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TYPE_VALUE);
            this.versionGameVersion = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.VERSION_VALUE);
            
            return true;
        } catch (final IOException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.plugin.getLogger().warning("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                this.plugin.getLogger().warning("Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
                this.plugin.getLogger().warning("The updater could not contact dev.bukkit.org for updating.");
                this.plugin.getLogger().warning("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                this.result = UpdateResult.FAIL_DBO;
            }
            return false;
        }
    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
        	
            if (Updater.this.url != null) {
                // Obtain the results of the project's file feed
                if (Updater.this.read()) {
                	if(Updater.this.versionName.replaceAll("v", "").split(" ")[1].equals(plugin.getDescription().getVersion()))
                		Updater.this.result = UpdateResult.NO_UPDATE;
                	else
                		Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                }
            }
        }
    }
}
