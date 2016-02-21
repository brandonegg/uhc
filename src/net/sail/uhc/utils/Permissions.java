package net.sail.uhc.utils;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

/**
 * Created by brand on 12/30/2015.
 */
public enum Permissions {

    GAME_ADMIN("sailmc.uhc.gameadmin"), DEFAULT_USER("sailmc.uhc.default");

    private Permission permission;

    Permissions(String permissionName) {
        this.permission = new Permission(permissionName);
    }

    public Permission getPermission() { return permission; }
}
