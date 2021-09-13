package de.spaceai.spaceperms;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.spaceai.spaceperms.api.handler.IPermissionHandler;
import de.spaceai.spaceperms.commands.bungeecord.RankCommand;
import de.spaceai.spaceperms.commands.spigot.RankSyncCommand;
import de.spaceai.spaceperms.config.Configuration;
import de.spaceai.spaceperms.database.MySQL;
import de.spaceai.spaceperms.listener.bungeecord.PermissionCheckListener;
import de.spaceai.spaceperms.listener.bungeecord.ServerConnectListener;
import de.spaceai.spaceperms.listener.spigot.PlayerLoginListener;
import de.spaceai.spaceperms.permission.PermissionHandler;
import de.spaceai.spaceperms.permission.spigot.SpacePermissibleInjection;
import de.spaceai.spaceperms.util.Logger;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SpacePerms {

    @Setter
    @Getter
    private static SpacePerms spacePerms;

    /**
     * Instance for Spigot Plugin
     */
    private JavaPlugin spigotInstance;

    /**
     * Instance for BungeeCord Plugin
     */
    private Plugin bungeeInstance;

    /**
     * init the logger
     */
    private final Logger logger;

    /**
     * init the configuation
     */
    private Configuration configuration;

    /**
     * Init the SpacePerms Database
     */
    private MySQL mySQL;

    /**
     * init the permission handler
     */
    private IPermissionHandler permissionHandler;

    /**
     * init for the spigot plugin
     * @param spigotInstance
     */
    @SuppressWarnings("unused")
    public SpacePerms(JavaPlugin spigotInstance) {
        this.logger = new Logger(this);
        this.spigotInstance = spigotInstance;
        this.bungeeInstance = null;
    }

    /**
     * init for bungeecord plugin
     * @param bungeeInstance
     */
    @SuppressWarnings("unused")
    public SpacePerms(Plugin bungeeInstance) {
        this.logger = new Logger(this);
        this.bungeeInstance = bungeeInstance;
        this.spigotInstance = null;
    }

    public void onLoad() {

    }

    public void onEnable() {

        this.logger.log("Plugin enabled.");

        this.configuration = new Configuration(new Configuration.ConfigPath("plugins/SpacePerms/",
                "config.json"), this);

        JsonObject jsonObject = this.configuration.get("mysql");
        String host = jsonObject.get("host").getAsString();
        String database = jsonObject.get("database").getAsString();
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        this.mySQL = new MySQL(this, host, database, username, password);
        this.mySQL.connect();

        this.mySQL.createTable("users", "id INT NOT NULL AUTO_INCREMENT", "uuid VARCHAR(50) NOT NULL",
                "name VARCHAR(50) NOT NULL", "rank VARCHAR(50) NOT NULL", "PRIMARY KEY (id)");

        this.mySQL.createTable("rank_permissions", "id INT NOT NULL AUTO_INCREMENT",
                "name VARCHAR(50) NOT NULL", "permission VARCHAR(9999)", "PRIMARY KEY (id)");

        this.mySQL.createTable("user_permissions", "id INT NOT NULL AUTO_INCREMENT",
                "uuid VARCHAR(50) NOT NULL", "permission VARCHAR(9999)", "PRIMARY KEY (id)");

        this.mySQL.createTable("ranks", "id INT NOT NULL AUTO_INCREMENT", "name VARCHAR(50)",
                "prefix VARCHAR(50)", "suffix VARCHAR(50)", "weight INT", "PRIMARY KEY (id)");

        if(!this.mySQL.hasElement("ranks", "name", "default")) {
            this.mySQL.update("INSERT INTO ranks(name, prefix, suffix, weight) VALUES " +
                    "('default', '', '', 99)");
        }

        this.permissionHandler = new PermissionHandler(this);
        this.permissionHandler.loadRanks();
        this.permissionHandler.loadUsers();

        if(this.isBungeeCord()) {
            this.registerBungeeCommand();
            this.registerBungeeListener();
        } else {
            this.registerSpigotListener();
            this.registerSpigotCommand();
        }

        //((PermissionHandler)this.permissionHandler).startSynchronizer();

        if(!isBungeeCord())
            Bukkit.getOnlinePlayers().forEach(player -> {
                SpacePermissibleInjection permissibleInjection = new SpacePermissibleInjection(player, this);
                permissibleInjection.inject();
            });

    }

    private void registerBungeeCommand() {
        this.bungeeInstance.getProxy().getPluginManager().registerCommand(
                this.bungeeInstance, new RankCommand(this)
        );
    }

    private void registerSpigotCommand() {
        this.spigotInstance.getCommand("spacesync").setExecutor(new RankSyncCommand(this));
    }

    private void registerBungeeListener() {
        this.bungeeInstance.getProxy().getPluginManager().registerListener(
                this.bungeeInstance, new ServerConnectListener(this)
        );
        this.bungeeInstance.getProxy().getPluginManager().registerListener(
                this.bungeeInstance, new PermissionCheckListener(this)
        );
    }

    private void registerSpigotListener() {
        this.spigotInstance.getServer().getPluginManager().registerEvents(
                new PlayerLoginListener(this),
                this.spigotInstance
        );
    }

    public void onDisable() {
        //((PermissionHandler)this.permissionHandler).stopSynchronzer();
    }

    public boolean isBungeeCord() {
        return this.bungeeInstance != null;
    }

}
