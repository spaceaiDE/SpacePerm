package de.spaceai.spaceperms.permission.entity;

import com.google.common.collect.Lists;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.api.permission.IPermission;
import de.spaceai.spaceperms.api.rank.IPermissionRank;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
public class PermissionPlayer implements IPermissionUser {

    private UUID uuid;
    private String username;
    private IPermissionRank permissionRank;
    private List<IPermission> userPermission;

    public PermissionPlayer() {
        this.uuid = null;
        this.username = null;
        this.permissionRank = null;
        this.userPermission = null;
    }

    public PermissionPlayer(UUID uuid, String username, IPermissionRank permissionRank, List<IPermission> userPermission) {
        this.uuid = uuid;
        this.username = username;
        this.permissionRank = permissionRank;
        this.userPermission = userPermission;
    }

    public PermissionPlayer(UUID uuid, String username, IPermissionRank permissionRank) {
        this.uuid = uuid;
        this.username = username;
        this.permissionRank = permissionRank;
        this.userPermission = Lists.newArrayList();
    }

    @Override
    public String getPermissionsAsString() {
        String permString = "";
        for (IPermission rankPermission : userPermission) {
            permString+=rankPermission.getPermission()+";";
        }
        if(permString.length() > 2)
            permString = permString.substring(0, permString.length()-1);
        return permString;
    }

    @Override
    public UUID getUniqueID() {
        return this.uuid;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public IPermissionRank getRank() {
        return this.permissionRank;
    }

    @Override
    public List<IPermission> getUserPermission() {
        return this.userPermission;
    }

    @Override
    public void addPermission(String permissionName) {
        this.userPermission.add(new Permission(permissionName));
    }

    @Override
    public void removePermission(String permissionName) {
        if(hasPermission(permissionName)) {
            IPermission iPermission = this.userPermission.stream().filter(permission ->
                    permission.getPermission().equals(permissionName)).findFirst().get();
            this.userPermission.remove(iPermission);
        }
    }

    @Override
    public boolean hasPermission(IPermission permission) {
        return this.userPermission.contains(permission);
    }

    @Override
    public boolean hasPermission(String permissionName) {
        if(this.userPermission.size() == 0)
            return false;
        for (IPermission iPermission : this.userPermission) {
            if(iPermission.getPermission().equalsIgnoreCase(permissionName)) {
                return true;
            }
        }
        return false;
    }
}
