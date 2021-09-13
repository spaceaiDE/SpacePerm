package de.spaceai.spaceperms.api.rank;

import de.spaceai.spaceperms.api.permission.IPermission;

import java.util.List;

public interface IPermissionRank {

    /**
     * @return name of rank
     */
    String getName();

    /**
     * @return prefix of rank
     */
    String getPrefix();

    /**
     * @return suffix of the rank
     */
    String getSuffix();

    /**
     * @return weight of the group
     */
    int getWeight();

    /**
     * @return list of permissions
     */
    List<IPermission> getPermissions();

    /**
     * @param permissionName adding permission to user
     */
    void addPermission(String permissionName);

    /**
     * @param permissionName remove permission from user
     */
    void removePermission(String permissionName);

    /**
     * @param permissionName
     * @return if rank has permission
     */
    boolean hasPermission(String permissionName);

    /**
     * @param permission
     * @return if rank has permission
     */
    boolean hasPermission(IPermission permission);

    /**
     *
     * @return permissions of user as string
     */
    String getPermissionsAsString();

}
