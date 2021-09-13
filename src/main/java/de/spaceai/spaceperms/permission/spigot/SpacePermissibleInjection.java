package de.spaceai.spaceperms.permission.spigot;

import de.spaceai.spaceperms.SpacePerms;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

@AllArgsConstructor
public class SpacePermissibleInjection {

    private final Player player;
    private final SpacePerms spacePerms;

    @SneakyThrows
    public void inject() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        Field field = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftHumanEntity").getDeclaredField("perm");
        field.setAccessible(true);
        field.set(this.player, new SpacePermissible(this.player, this.spacePerms));
    }

}
