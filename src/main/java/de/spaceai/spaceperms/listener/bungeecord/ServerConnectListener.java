package de.spaceai.spaceperms.listener.bungeecord;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.permission.entity.PermissionPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectListener implements Listener {

    private final SpacePerms spacePerms;

    public ServerConnectListener(SpacePerms spacePerms) {
        this.spacePerms = spacePerms;
    }

    @EventHandler
    public void serverConnect(ServerConnectEvent loginEvent) {
        ProxiedPlayer proxiedPlayer = loginEvent.getPlayer();
        IPermissionUser permissionUser = spacePerms.getPermissionHandler().getCachedPermissionUser(proxiedPlayer.getUniqueId());
        if(permissionUser == null) {
            spacePerms.getPermissionHandler().createUser(proxiedPlayer);
        } else {
            if(!permissionUser.getUsername().equals(proxiedPlayer.getName())) {
                ((PermissionPlayer)permissionUser).setUsername(proxiedPlayer.getName());
                this.spacePerms.getMySQL().update(
                        "UPDATE users SET name='"+proxiedPlayer.getName()+"' WHERE uuid='"+proxiedPlayer.
                                getUniqueId().toString()+"'"
                );
            }
            this.spacePerms.getPermissionHandler().updateRank(permissionUser.getRank());
            this.spacePerms.getPermissionHandler().updateUser(permissionUser);
        }
    }


}
