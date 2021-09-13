package de.spaceai.spaceperms.commands.bungeecord;

import de.spaceai.spaceperms.SpacePerms;
import de.spaceai.spaceperms.api.entity.IPermissionUser;
import de.spaceai.spaceperms.api.handler.IPermissionHandler;
import de.spaceai.spaceperms.api.rank.IPermissionRank;
import de.spaceai.spaceperms.permission.PermissionHandler;
import de.spaceai.spaceperms.permission.entity.PermissionPlayer;
import de.spaceai.spaceperms.permission.entity.PermissionRank;
import de.spaceai.spaceperms.util.MESSAGES;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class RankCommand extends Command {

    private final SpacePerms spacePerms;
    private final IPermissionHandler permissionHandler;
    private final String prefix = MESSAGES.PREFIX.getText();

    public RankCommand(SpacePerms spacePerms) {
        super("rank", "spaceperms.admin");
        this.spacePerms = spacePerms;
        this.permissionHandler = this.spacePerms.getPermissionHandler();
        this.setPermissionMessage(prefix+"§4Dazu hast du keine Berechtigungen.");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("create")) {
                IPermissionRank permissionRank = this.permissionHandler.getCachedPermissionRank(args[0]);
                if(permissionRank != null) {
                    commandSender.sendMessage(prefix + "§cDieser Rang existiert bereits!");
                    return;
                }
                this.permissionHandler.createRank(args[0]);
                commandSender.sendMessage(prefix+"§b"+args[0]+" §7wurde §aerfolgreich §7erstellt.");
            } else if(args[1].equalsIgnoreCase("delete")) {
                IPermissionRank permissionRank = this.permissionHandler.getCachedPermissionRank(args[0]);
                if(permissionRank == null) {
                    commandSender.sendMessage(prefix + "§cDieser Rang existiert nicht!");
                    return;
                }
                ((PermissionHandler)this.permissionHandler).getPermissionRankCache().invalidate(permissionRank.getName());
                this.spacePerms.getMySQL().update(
                        "DELETE FROM ranks WHERE name='"+permissionRank.getName()+"'"
                );
                commandSender.sendMessage(prefix+"§b"+args[0]+" §7wurde §aerfolgreich §7gelöscht.");
            }
            // /rank rank <rankName> <permission/prefix/suffix> <add/remove> <permission>
        } else if(args.length >= 5) {
            if(args[0].equalsIgnoreCase("group")) {
                String rankName = args[1];
                IPermissionRank permissionRank = this.permissionHandler.getCachedPermissionRank(rankName);
                if(permissionRank == null) {
                    commandSender.sendMessage(prefix+"§cDieser Rang existiert nicht!");
                    return;
                }
                if(args[2].equalsIgnoreCase("permission")) {
                    if(args[3].equalsIgnoreCase("add")) {
                        String permission = args[4];
                        if(permissionRank.hasPermission(permission)) {
                            commandSender.sendMessage(prefix+"§cDieser Gruppe besitzt bereits diese Berechtigung");
                            return;
                        }
                        permissionRank.addPermission(permission);
                        commandSender.sendMessage(prefix+"§b"+permission+" §7wurde §aerfolgreich §7zu §b"+
                                permissionRank.getName()+" §7hinzugefügt.");
                        this.spacePerms.getMySQL().update(
                                "INSERT INTO rank_permissions(name, permission) VALUES ('"+permissionRank.getName()+"'," +
                                        "'"+permission+"')"
                        );
                    } else if(args[3].equalsIgnoreCase("remove")) {
                        String permission = args[4];
                        if(!permissionRank.hasPermission(permission)) {
                            commandSender.sendMessage(prefix+"§cDieser Gruppe besitzt diese Berechtigung nicht");
                            return;
                        }
                        permissionRank.removePermission(permission);
                        commandSender.sendMessage(prefix+"§b"+permission+" §7wurde §aerfolgreich §7von §b"+
                                permissionRank.getName()+" §7entfernt.");
                        this.spacePerms.getMySQL().update(
                                "DELETE FROM rank_permissions WHERE name='"+permissionRank.getName()+"'" +
                                        " AND permission='"+permission+"'"
                        );
                    } else {
                        this.sendHelp(commandSender);
                    }
                } else if(args[2].equalsIgnoreCase("prefix")) {
                    if(args[3].equalsIgnoreCase("set")) {
                        String prefix = "";
                        for(int i = 4; i < args.length; i++)
                            prefix+=args[i]+" ";
                        if(prefix.length() > 2)
                            prefix.substring(0, prefix.length()-1);
                        ((PermissionRank)permissionRank).setPrefix(prefix);
                        commandSender.sendMessage(prefix+"§b"+prefix.replaceAll("&","§")+" §7wurde §aerfolgreich §7zu Prefix von §b"+
                                permissionRank.getName()+" §7gesetzt.");
                        this.spacePerms.getMySQL().update(
                                "UPDATE ranks SET prefix='"+permissionRank.getPrefix()+"'" +
                                        " WHERE name='"+permissionRank.getName()+"'"
                        );
                    } else this.sendHelp(commandSender);
                } else if(args[2].equalsIgnoreCase("suffix")) {
                    if(args[3].equalsIgnoreCase("set")) {
                        String suffix = "";
                        for(int i = 4; i < args.length; i++)
                            suffix+=args[i]+" ";
                        if(suffix.length() > 2)
                            suffix.substring(0, suffix.length()-1);
                        ((PermissionRank)permissionRank).setSuffix(suffix);
                        commandSender.sendMessage(prefix+"§b"+suffix.replaceAll("&","§")+" §7wurde §aerfolgreich §7zu Suffix von §b"+
                                permissionRank.getName()+" §7gesetzt.");
                        this.spacePerms.getMySQL().update(
                                "UPDATE ranks SET suffix='"+permissionRank.getSuffix()+"'" +
                                        " WHERE name='"+permissionRank.getName()+"'"
                        );
                    } else this.sendHelp(commandSender);
                } else if(args[2].equalsIgnoreCase("weight")) {
                    if(args[3].equalsIgnoreCase("set")) {
                        try {
                            int weight = Integer.parseInt(args[4]);
                            ((PermissionRank)permissionRank).setWeight(weight);
                            this.spacePerms.getMySQL().update(
                                    "UPDATE ranks SET weight="+weight+"" +
                                            " WHERE name='"+permissionRank.getName()+"'"
                            );
                            commandSender.sendMessage(prefix+"Gewicht von §b"+permissionRank.getName()+
                                    "§7 wurde §aerfolgreich zu §b"+weight+" §7gesetezt");
                        }catch (Exception e) {
                            commandSender.sendMessage(prefix+"§4Gib eine gültige Zahl an.");
                        }
                    } else this.sendHelp(commandSender);
                } else sendHelp(commandSender);
            } else if(args[0].equalsIgnoreCase("user")) {
                String userName = args[1];
                IPermissionUser permissionUser = this.permissionHandler.getCachedPermissionUser(userName);
                if(permissionUser == null) {
                    commandSender.sendMessage(prefix+"§cDieser User existiert nicht!");
                    return;
                }
                if(args[2].equalsIgnoreCase("permission")) {
                    if (args[3].equalsIgnoreCase("add")) {
                        String permission = args[4];
                        if (permissionUser.hasPermission(permission)) {
                            commandSender.sendMessage(prefix + "§cDieser User besitzt bereits diese Berechtigung");
                            return;
                        }
                        permissionUser.addPermission(permission);
                        commandSender.sendMessage(prefix + "§b" + permission + " §7wurde §aerfolgreich §7zu §b" +
                                permissionUser.getUsername() + " §7hinzugefügt.");
                        this.spacePerms.getMySQL().update(
                                "INSERT INTO user_permissions(uuid, permission) VALUES ('"+permissionUser.
                                        getUniqueID().toString()+"', '"+permission+"')"
                        );
                    } else if (args[3].equalsIgnoreCase("remove")) {
                        String permission = args[4];
                        if (!permissionUser.hasPermission(permission)) {
                            commandSender.sendMessage(prefix + "§cDieser User besitzt diese Berechtigung nicht");
                            return;
                        }
                        permissionUser.removePermission(permission);
                        commandSender.sendMessage(prefix + "§b" + permission + " §7wurde §aerfolgreich §7von §b" +
                                permissionUser.getUsername() + " §7entfernt.");
                        this.spacePerms.getMySQL().update(
                                "DELETE FROM user_permissions WHERE uuid='"+permissionUser.getUniqueID().toString()+"'" +
                                        " AND permission='"+permission+"'"
                        );
                    } else {
                        this.sendHelp(commandSender);
                    }
                } else if(args[2].equalsIgnoreCase("group")) {
                    if(args[3].equalsIgnoreCase("set")) {
                        IPermissionRank permissionRank = permissionHandler.getCachedPermissionRank(args[4]);
                        if(permissionRank == null) {
                            commandSender.sendMessage(prefix+"§4Diese Gruppe existiert nicht");
                            return;
                        }
                        commandSender.sendMessage(prefix+"§b"+permissionUser.getUsername()+" §7besitzt nun die" +
                                " Gruppe §b"+permissionRank.getName());
                        ((PermissionPlayer) permissionUser).setPermissionRank(permissionRank);
                        spacePerms.getMySQL().update(
                                "UPDATE users SET rank='"+permissionRank.getName()+"' WHERE uuid='"+
                                        permissionUser.getUniqueID().toString()+"'"
                        );
                    } else this.sendHelp(commandSender);
                } else this.sendHelp(commandSender);
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("sync")) {
                long current = System.currentTimeMillis();
                commandSender.sendMessage(prefix+"Sync Daten...");
                ((PermissionHandler)this.permissionHandler).sync();
                commandSender.sendMessage(prefix+"§aSync abgeschlossen! ("+
                        (System.currentTimeMillis() - current)+"ms)");
            }
        } else this.sendHelp(commandSender);
    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessages(prefix + "/rank sync");
        commandSender.sendMessages(prefix + "/rank <groupName> create");
        commandSender.sendMessages(prefix + "/rank <groupName> delete");
        commandSender.sendMessages(prefix + "/rank group <groupName> permission add <text>");
        commandSender.sendMessages(prefix + "/rank group <groupName> permission remove <text>");
        commandSender.sendMessages(prefix + "/rank group <groupName> prefix set <text>");
        commandSender.sendMessages(prefix + "/rank group <groupName> weight set <text>");
        commandSender.sendMessages(prefix + "/rank user <userName> permission add <text>");
        commandSender.sendMessages(prefix + "/rank user <userName> permission remove <text>");
        commandSender.sendMessages(prefix + "/rank user <userName> group set <groupName>");
        commandSender.sendMessages(prefix + "§aEmpfohlen: §7Wenn eine Änderung vorliegt nochmal mit dem Server neuverbinden.");
    }
}
