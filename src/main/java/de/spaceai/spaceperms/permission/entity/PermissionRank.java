package de.spaceai.spaceperms.permission.entity;

import de.spaceai.spaceperms.api.permission.IPermission;
import de.spaceai.spaceperms.api.rank.IPermissionRank;
import lombok.Setter;

import java.util.List;

@Setter
public class PermissionRank implements IPermissionRank {

    private String prefix, suffix, name;
    private int weight;
    private List<IPermission> rankPermissions;

    @Override
    public String getPermissionsAsString() {
        String permString = "";
        for (IPermission rankPermission : rankPermissions) {
            permString+=rankPermission.getPermission()+";";
        }
        if(permString.length() > 2)
            permString = permString.substring(0, permString.length()-1);
        return permString;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public List<IPermission> getPermissions() {
        return this.rankPermissions;
    }

    @Override
    public void addPermission(String permissionName) {
        this.rankPermissions.add(new Permission(permissionName));
    }

    @Override
    public void removePermission(String permissionName) {
        if(hasPermission(permissionName)) {
            IPermission iPermission = this.rankPermissions.stream().filter(permission ->
                    permission.getPermission().equals(permissionName)).findFirst().get();
            this.rankPermissions.remove(iPermission);
        }
    }

    @Override
    public boolean hasPermission(IPermission permission) {
        return this.rankPermissions.contains(permission);
    }

    @Override
    public boolean hasPermission(String permissionName) {
        for (IPermission rankPermission : this.rankPermissions) {
            if(rankPermission.getPermission().equalsIgnoreCase(permissionName))
                return true;
        }
        return false;
    }
}
