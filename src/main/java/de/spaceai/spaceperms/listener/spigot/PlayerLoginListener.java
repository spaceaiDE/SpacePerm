package de.spaceai.spaceperms.listener.spigot;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.permission.spigot.SpacePermissibleInjection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    private final SpacePerms spacePerms;

    public PlayerLoginListener(SpacePerms spacePerms) {
        this.spacePerms = spacePerms;
    }

    @EventHandler
    public void playerLogin(PlayerLoginEvent loginEvent) {
        Player player = loginEvent.getPlayer();
        SpacePermissibleInjection injection = new SpacePermissibleInjection(player, this.spacePerms);
        injection.inject();
        IPermissionUser permissionUser = spacePerms.getPermissionHandler().getCachedPermissionUser(player.getUniqueId());
        if(permissionUser == null) {
            this.spacePerms.getPermissionHandler().loadSingleUsers(player.getUniqueId());
        } else {
            this.spacePerms.getPermissionHandler().updateRank(permissionUser.getRank());
            this.spacePerms.getPermissionHandler().updateUser(permissionUser);
        }

    }

}
