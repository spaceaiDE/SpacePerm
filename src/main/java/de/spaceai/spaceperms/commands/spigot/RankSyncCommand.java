package de.spaceai.spaceperms.commands.spigot;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.permission.PermissionHandler;
import de.spaceai.spaceperms.util.MESSAGES;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RankSyncCommand implements CommandExecutor {

    private final SpacePerms spacePerms;
    private final String prefix = MESSAGES.PREFIX.getText();

    public RankSyncCommand(SpacePerms spacePerms) {
        this.spacePerms = spacePerms;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!commandSender.hasPermission("spaceperms.sync")) {
            commandSender.sendMessage(prefix+"§cDazu hast du keine Berechtigungen.");
            return true;
        }
        long current = System.currentTimeMillis();
        commandSender.sendMessage(prefix+"Sync Daten...");
        ((PermissionHandler)this.spacePerms.getPermissionHandler()).sync();
        commandSender.sendMessage(prefix+"§aSync abgeschlossen! ("+
                (System.currentTimeMillis() - current)+"ms)");
        return false;
    }
}
