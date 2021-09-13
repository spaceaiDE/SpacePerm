package de.spaceai.spaceperms.bootstrap;

import de.spaceai.spaceperms.SpacePerms;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Constructor;

public class BungeeBootstrap extends Plugin {

    private SpacePerms spacePerms;

    @Override
    @SneakyThrows
    public void onLoad() {

        Constructor<SpacePerms> constructor = SpacePerms.class.getDeclaredConstructor(Plugin.class);
        constructor.setAccessible(true);
        this.spacePerms = constructor.newInstance(this);
        this.spacePerms.onLoad();

        SpacePerms.setSpacePerms(this.spacePerms);

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
