package xyz.artuto.authserver.modules;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.artuto.authserver.AuthServer;

import java.util.List;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public class JoinModule implements Listener
{
    private final ConfigurationSection joinSection;
    private final ConfigurationSection spawnLocationSection;
    private final ConfigurationSection leaveSection;

    public JoinModule(AuthServer plugin)
    {
        this.joinSection = plugin.getConfig().getConfigurationSection("join_message");
        this.spawnLocationSection = plugin.getConfig().getConfigurationSection("on_join_teleport");
        this.leaveSection = plugin.getConfig().getConfigurationSection("leave_message");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (spawnLocationSection.getBoolean("enabled")) {
            double x = this.spawnLocationSection.getDouble("x");
            double y = this.spawnLocationSection.getDouble("y");
            double z = this.spawnLocationSection.getDouble("z");
            float yaw = (float) this.spawnLocationSection.getDouble("yaw");
            float pitch = (float) this.spawnLocationSection.getDouble("pitch");

            player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
        }


        String globalMessage = joinSection.getString("global");
        List<String> lines = joinSection.getStringList("message");

        event.setJoinMessage(prepareMessage(globalMessage, player));

        if(!(joinSection.getBoolean("enabled")) || lines.isEmpty())
            return;

        StringBuilder sb = new StringBuilder();
        for(String line : lines)
            sb.append(line).append("\n");

        TextComponent text = new TextComponent(prepareMessage(sb.toString(), player));
        player.sendMessage(text);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        String globalMessage = leaveSection.getString("global");
        event.setQuitMessage(prepareMessage(globalMessage, event.getPlayer()));
    }

    private String prepareMessage(String message, Player player)
    {
        return translateAlternateColorCodes('&', message).replace("%player%", player.getName());
    }
}
