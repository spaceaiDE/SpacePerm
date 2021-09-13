package de.spaceai.spaceperms.listener.bungeecord;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionCheckListener implements Listener {

    private final SpacePerms spacePerms;

    public PermissionCheckListener(SpacePerms spacePerms) {
        this.spacePerms = spacePerms;
    }

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent event) {
        if(!(event.getSender() instanceof ProxiedPlayer)) {
            event.setHasPermission(true);
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
        IPermissionUser permissionUser = spacePerms.getPermissionHandler().getCachedPermissionUser(proxiedPlayer.getUniqueId());
        if(permissionUser == null) {
            event.setHasPermission(false);
            return;
        }
        if(permissionUser.hasPermission("*")) {
            event.setHasPermission(true);
            return;
        }
        if(permissionUser.getRank().hasPermission("*")) {
            event.setHasPermission(true);
            return;
        }
        if(!permissionUser.hasPermission(event.getPermission())) {
            event.setHasPermission(permissionUser.getRank().hasPermission(event.getPermission()));
        } else event.setHasPermission(true);
    }

}
