package de.spaceai.spaceperms.bootstrap;

import de.spaceai.spaceperms.SpacePerms;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public class SpigotBootstrap extends JavaPlugin {

    private SpacePerms spacePerms;

    @Override
    @SneakyThrows
    public void onLoad() {

        Constructor<SpacePerms> constructor = SpacePerms.class.getDeclaredConstructor(JavaPlugin.class);
        constructor.setAccessible(true);
        this.spacePerms = constructor.newInstance(this);

        SpacePerms.setSpacePerms(this.spacePerms);

        this.spacePerms.onLoad();

    }

    @Override
    public void onEnable() {
        this.spacePerms.onEnable();
    }

    @Override
    public void onDisable() {
        this.spacePerms.onDisable();
    }
}
