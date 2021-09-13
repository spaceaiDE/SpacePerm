package de.spaceai.spaceperms.util;

import de.spaceai.spaceperms.SpacePerms;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class Logger {

    private final SpacePerms spacePerms;

    public void log(String message) {
        String prefix = (spacePerms.isBungeeCord()) ? "BungeeSpacePerms" : "SpigotSpacePerms";
        String time = new Date().toLocaleString();
        System.out.println("["+prefix+"]["+time+"][LOG] "+message);
    }

    public void debug(String message) {
        String prefix = (spacePerms.isBungeeCord()) ? "BungeeSpacePerms" : "SpigotSpacePerms";
        String time = new Date().toLocaleString();
        System.out.println("["+prefix+"]["+time+"][DEBUG] "+message);
    }

    public void warn(String message) {
        String prefix = (spacePerms.isBungeeCord()) ? "BungeeSpacePerms" : "SpigotSpacePerms";
        String time = new Date().toLocaleString();
        System.out.println("["+prefix+"]["+time+"][WARN] "+message);
    }

    public void error(String message) {
        String prefix = (spacePerms.isBungeeCord()) ? "BungeeSpacePerms" : "SpigotSpacePerms";
        String time = new Date().toLocaleString();
        System.out.println("["+prefix+"]["+time+"][ERROR] "+message);
    }

}
