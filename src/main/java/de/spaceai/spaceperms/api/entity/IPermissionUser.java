package de.spaceai.spaceperms.api.entity;

import de.spaceai.spaceperms.api.permission.IPermission;
import de.spaceai.spaceperms.api.rank.IPermissionRank;

import java.util.List;
import java.util.UUID;

public interface IPermissionUser {

    /**
     * @return uniqueID of the User
     */
    UUID getUniqueID();

    /**
     * @return name of user
     */
    String getUsername();

    /**
     * @return rank of permission user
     */
    IPermissionRank getRank();

    /**
     * @return list of player permission
     */
    List<IPermission> getUserPermission();

    /**
     * @param permissionName adding permission to user
     */
    void addPermission(String permissionName);

    /**
     * @param permissionName remove permission from user
     */
    void removePermission(String permissionName);

    /**
     * @param permission
     * @return player has permission
     */
    boolean hasPermission(IPermission permission);

    /**
     * @param permissionName
     * @return player has permission
     */
    boolean hasPermission(String permissionName);

    /**
     *
     * @return permissions of user as string
     */
    String getPermissionsAsString();

}
