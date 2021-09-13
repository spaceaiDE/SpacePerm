package de.spaceai.spaceperms.permission.spigot;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

public class SpacePermissible extends PermissibleBase {

    private final Player player;
    private final SpacePerms spacePerms;

    public SpacePermissible(Player opable, SpacePerms spacePerms) {
        super(opable);
        this.player = opable;
        this.spacePerms = spacePerms;
    }

    @Override
    public boolean hasPermission(String inName) {
        try {
            IPermissionUser iPermissionUser = this.spacePerms.getPermissionHandler()
                    .getCachedPermissionUser(this.player.getUniqueId());
            if(iPermissionUser.hasPermission("*"))
                return true;
            if(iPermissionUser.getRank().hasPermission("*"))
                return true;
            if(iPermissionUser.hasPermission(inName))
                return true;
            if(iPermissionUser.getRank().hasPermission(inName))
                return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
