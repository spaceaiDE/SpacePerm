package de.spaceai.spaceperms.permission;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.api.handler.IPermissionHandler;
import de.spaceai.spaceperms.api.permission.IPermission;
import de.spaceai.spaceperms.api.rank.IPermissionRank;
import de.spaceai.spaceperms.database.MySQL;
import de.spaceai.spaceperms.permission.entity.Permission;
import de.spaceai.spaceperms.permission.entity.PermissionPlayer;
import de.spaceai.spaceperms.permission.entity.PermissionRank;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class PermissionHandler implements IPermissionHandler {

    private final MySQL mySQL;
    private final SpacePerms spacePerms;
    private Cache<String, IPermissionRank> permissionRankCache;
    private Cache<UUID, IPermissionUser> permissionUserCache;
    private Thread syncThread;

    public PermissionHandler(SpacePerms spacePerms) {
        this.spacePerms = spacePerms;
        this.mySQL = spacePerms.getMySQL();
        this.permissionRankCache = CacheBuilder.newBuilder().build();
        this.permissionUserCache = CacheBuilder.newBuilder().build();
    }

    @Override
    @SneakyThrows
    public void loadUsers() {
        ResultSet resultSet = this.mySQL.get("SELECT * FROM users");
        while(resultSet.next()) {
            PermissionPlayer player = new PermissionPlayer();
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            player.setUuid(uuid);
            player.setUsername(resultSet.getString("name"));
            player.setPermissionRank(getCachedPermissionRank(resultSet.getString("rank")));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM user_permissions WHERE uuid='"+uuid.toString()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            this.permissionUserCache.put(uuid, player);
        }
    }

    @Override
    @SneakyThrows
    public void loadRanks() {
        ResultSet resultSet = this.mySQL.get("SELECT * FROM ranks");
        while(resultSet.next()) {
            PermissionRank permissionRank = new PermissionRank();
            String name = resultSet.getString("name");
            permissionRank.setName(name);
            permissionRank.setPrefix(resultSet.getString("prefix"));
            permissionRank.setSuffix(resultSet.getString("suffix"));
            permissionRank.setWeight(resultSet.getInt("weight"));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM rank_permissions WHERE name='"+permissionRank
                    .getName()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            permissionRank.setRankPermissions(permissions);
            this.permissionRankCache.put(name, permissionRank);
        }
    }

    @Override
    @SneakyThrows
    public void loadSingleRank(String name) {
        if(this.permissionRankCache.asMap().containsKey(name))
            return;
        ResultSet resultSet = this.mySQL.get("SELECT * FROM ranks WHERE name='"+name+"'");
        if(resultSet.next()) {
            PermissionRank permissionRank = new PermissionRank();
            permissionRank.setName(resultSet.getString("name"));
            permissionRank.setPrefix(resultSet.getString("prefix"));
            permissionRank.setSuffix(resultSet.getString("suffix"));
            permissionRank.setWeight(resultSet.getInt("weight"));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM rank_permissions WHERE uuid='"+
                    permissionRank.getName()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            permissionRank.setRankPermissions(permissions);
            this.permissionRankCache.put(permissionRank.getName(), permissionRank);
        }
    }

    @Override
    @SneakyThrows
    public void loadSingleUsers(UUID uuid) {
        if(this.permissionUserCache.asMap().containsKey(uuid))
            return;
        ResultSet resultSet = this.mySQL.get("SELECT * FROM users WHERE uuid='"+uuid.toString()+"'");
        if(resultSet.next()) {
            PermissionPlayer player = new PermissionPlayer();
            player.setUuid(uuid);
            player.setUsername(resultSet.getString("name"));
            player.setPermissionRank(getCachedPermissionRank(resultSet.getString("rank")));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM user_permissions WHERE uuid='"+uuid.toString()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            player.setUserPermission(permissions);
            this.permissionUserCache.invalidate(uuid);
            this.permissionUserCache.put(uuid, player);
        }
    }

    @Override
    @SneakyThrows
    public void updateRank(IPermissionRank rank) {
        PermissionRank permissionRank = (PermissionRank) rank;
        ResultSet resultSet = this.mySQL.get("SELECT * FROM ranks WHERE name='"+rank.getName()+"'");
        if(resultSet.next()) {
            permissionRank.setPrefix(resultSet.getString("prefix"));
            permissionRank.setSuffix(resultSet.getString("suffix"));
            permissionRank.setWeight(resultSet.getInt("weight"));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM rank_permissions WHERE name='"+permissionRank.getName()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            permissionRank.setRankPermissions(permissions);
            this.permissionRankCache.invalidate(permissionRank.getName());
            this.permissionRankCache.put(permissionRank.getName(), permissionRank);
        }
    }

    @Override
    @SneakyThrows
    public void updateUser(IPermissionUser permissionUser) {
        PermissionPlayer permissionPlayer = (PermissionPlayer) permissionUser;
        ResultSet resultSet = this.mySQL.get("SELECT * FROM users WHERE uuid='"+permissionPlayer.
                getUniqueID().toString()+"'");
        if(resultSet.next()) {
            permissionPlayer.setUsername(resultSet.getString("name"));
            permissionPlayer.setPermissionRank(getCachedPermissionRank(resultSet.getString("rank")));
            List<IPermission> permissions = Lists.newArrayList();
            ResultSet permissionSet = this.mySQL.get("SELECT permission FROM user_permissions WHERE uuid='"+permissionPlayer.getUniqueID()
                    .toString()+"'");
            while(permissionSet.next()) permissions.add(new Permission(permissionSet.getString("permission")));
            permissionPlayer.setUserPermission(permissions);
        }
    }

    @Override
    public void createRank(String name) {
        if(this.permissionRankCache.asMap().containsKey(name))
            return;
        PermissionRank permissionRank = new PermissionRank();
        permissionRank.setRankPermissions(Lists.newArrayList());
        permissionRank.setPrefix("");
        permissionRank.setSuffix("");
        permissionRank.setName(name);
        this.mySQL.update("INSERT INTO ranks(name, prefix, suffix, weight) VALUES ('"+
                permissionRank.getName()+"', '', '', 0)");
        this.permissionRankCache.put(name, permissionRank);
    }

    @Override
    public void createUser(ProxiedPlayer proxiedPlayer) {
        if(this.permissionUserCache.asMap().containsKey(proxiedPlayer.getUniqueId()))
            return;
        PermissionPlayer permissionPlayer = new PermissionPlayer();
        permissionPlayer.setUsername(proxiedPlayer.getName());
        permissionPlayer.setUuid(proxiedPlayer.getUniqueId());
        permissionPlayer.setPermissionRank(getCachedPermissionRank("default"));
        permissionPlayer.setUserPermission(Lists.newArrayList());
        mySQL.update("INSERT INTO users(uuid, name, rank) VALUES ('"+permissionPlayer.getUniqueID()
                .toString()+"', '"+permissionPlayer.getUsername()+"', 'default')");
        this.permissionUserCache.put(permissionPlayer.getUniqueID(), permissionPlayer);
    }

    @Override
    public void createUser(Player player) {
        if(this.permissionUserCache.asMap().containsKey(player.getUniqueId()))
            return;
        PermissionPlayer permissionPlayer = new PermissionPlayer();
        permissionPlayer.setUsername(player.getName());
        permissionPlayer.setUuid(player.getUniqueId());
        permissionPlayer.setPermissionRank(getCachedPermissionRank("default"));
        permissionPlayer.setUserPermission(Lists.newArrayList());
        mySQL.update("INSERT INTO users(uuid, name, rank) VALUES ('"+permissionPlayer.getUniqueID()
                .toString()+"', '"+permissionPlayer.getUsername()+"', 'default')");
        this.permissionUserCache.put(permissionPlayer.getUniqueID(), permissionPlayer);
    }

    @Override
    public IPermissionUser getCachedPermissionUser(UUID uuid) {
        return this.permissionUserCache.getIfPresent(uuid);
    }

    @Override
    public IPermissionUser getCachedPermissionUser(String name) {
        return (this.permissionUserCache.asMap().values().stream().filter(
                user -> user.getUsername().equals(name)
        ).findFirst().isPresent()) ? this.permissionUserCache.asMap().values().stream().filter(
                user -> user.getUsername().equals(name)
        ).findFirst().get() : null;
    }

    public void sync() {
        if(permissionRankCache != null && permissionRankCache.asMap().size() > 0)
            permissionRankCache.asMap().values().forEach(permissionRank -> updateRank(permissionRank));
        if(permissionUserCache != null && permissionUserCache.asMap().size() > 0)
            permissionUserCache.asMap().values().forEach(permissionUser -> updateUser(permissionUser));
    }

    @SneakyThrows
    public void startSynchronizer() {
        this.syncThread = new Thread(() -> {
            while(true) {
                if(permissionRankCache != null && permissionRankCache.asMap().size() > 0)
                    permissionRankCache.asMap().values().forEach(permissionRank -> updateRank(permissionRank));
                if(permissionUserCache != null && permissionUserCache.asMap().size() > 0)
                    permissionUserCache.asMap().values().forEach(permissionUser -> updateUser(permissionUser));
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.syncThread.start();
    }

    @Override
    public Collection<IPermissionRank> getCachedPermissionRanks() {
        return this.permissionRankCache.asMap().values();
    }

    public void stopSynchronzer() {
        this.syncThread.stop();
    }

    @Override
    public IPermissionRank getCachedPermissionRank(String name) {
        return this.permissionRankCache.getIfPresent(name);
    }
}
