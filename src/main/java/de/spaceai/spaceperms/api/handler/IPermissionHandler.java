package de.spaceai.spaceperms.api.handler;

import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.api.permission.IPermission;
import de.spaceai.spaceperms.api.rank.IPermissionRank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPermissionHandler {

    /**
     * Loading all avaible users
     */
    void loadUsers();

    /**
     * loading single user by uuid
     * @param uuid
     */
    void loadSingleUsers(UUID uuid);

    /**
     * load the rank
     * @param name
     */
    void loadSingleRank(String name);

    /**
     * load all ranks
     */
    void loadRanks();

    /**
     * Creating user by bungeecord instance
     * @param proxiedPlayer
     */
    void createUser(ProxiedPlayer proxiedPlayer);

    /**
     * @param name create rank
     */
    void createRank(String name);

    /**
     * Creating user by spigot instance
     * @param player
     */
    void createUser(Player player);

    /**
     * Synchronize with database
     * @param permissionRank
     */
    void updateRank(IPermissionRank permissionRank);

    /**
     * Synchronize with database
     */
    void updateUser(IPermissionUser permissionUser);

    /**
     * @param uuid
     * @return get cache permission user by uuid
     */
    IPermissionUser getCachedPermissionUser(UUID uuid);

    /**
     * @param name
     * @return get cache permission user by name
     */
    IPermissionUser getCachedPermissionUser(String name);

    /**
     * @param name
     * @return cached permission rank by name
     */
    IPermissionRank getCachedPermissionRank(String name);

    /**
     * @return list of the ranks.
     */
    Collection<IPermissionRank> getCachedPermissionRanks();

}
