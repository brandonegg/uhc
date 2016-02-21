package net.sail.uhc.utils;

import net.sail.uhc.settings.GameSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class Messaging {

    private static GameSettings gameSettings;

    private static final int CENTER_PX = 154;

    public enum Tag {
        ERROR(ChatColor.RED + "" + ChatColor.BOLD + "ERROR " + ChatColor.GRAY), IMPORTANT(ChatColor.YELLOW + "" + ChatColor.BOLD + "IMPORTANT " + ChatColor.GRAY), SUCCESS(ChatColor.GREEN + "" + ChatColor.BOLD + "SUCCESS! " + ChatColor.GRAY), ALERT(ChatColor.GREEN + "" + ChatColor.BOLD + "ALERT " + ChatColor.GRAY);

        private String tag;

        Tag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    public Messaging(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public static String secondsToTime(Integer amount) {
        String minutes = "00";
        String seconds = "00";

        if (amount >= 60) {
            Integer minutesNum = Math.abs(amount/60);
            if (minutesNum < 10) {
                minutes = ("0"+minutesNum.toString());
            } else {
                minutes = minutesNum.toString();
            }
            Integer secondsNum = amount-minutesNum*60;
            if (secondsNum < 10) {
                seconds = "0"+secondsNum.toString();
            } else {
                seconds = secondsNum.toString();
            }
        } else {
            if (amount < 10) {
                seconds = "0"+amount.toString();
            } else {
                seconds = amount.toString();
            }
        }

        return (minutes+":"+seconds);
    }

    public static void sendCenteredMessage(Player player, String message){
        if(message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('§', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public static void sendHostMessage(String message) {
        UUID hostUUID = gameSettings.getHost();
        if (hostUUID != null || Bukkit.getPlayer(hostUUID) !=null) {
            Player host = Bukkit.getPlayer(hostUUID);
            if (host.isOnline()) {
                host.sendMessage(ChatColor.GRAY + "["+ChatColor.RED + "HOST ALERT"+ChatColor.GRAY+"]"+ChatColor.WHITE +": " + message);
            }
        }
    }
}
