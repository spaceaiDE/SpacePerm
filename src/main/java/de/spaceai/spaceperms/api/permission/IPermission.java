package de.spaceai.spaceperms.api.permission;

public interface IPermission {

    /**
     * @return name of permission
     */
    String getPermission();

    /**
     * @return if permission is active
     */
    boolean isActive();

}
