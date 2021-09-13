package de.spaceai.spaceperms.permission.entity;

import de.spaceai.spaceperms.api.permission.IPermission;

public class Permission implements IPermission {

    private final String permissionName;
    private final boolean active;

    public Permission(String permissionName) {
        this.active = permissionName.startsWith("-");
        this.permissionName = (permissionName.startsWith("-")) ? permissionName.replaceAll("-", "") :
                permissionName;
    }

    @Override
    public String getPermission() {
        return this.permissionName;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }
}
