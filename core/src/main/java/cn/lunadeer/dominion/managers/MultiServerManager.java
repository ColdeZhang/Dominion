package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.Scheduler;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lunadeer.dominion.managers.TeleportManager.handleTeleportBcMsg;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;

// https://docs.papermc.io/paper/dev/plugin-messaging#forward
public class MultiServerManager implements PluginMessageListener {

    public static class MultiServerManagerText extends ConfigurationPart {
        public String sendingNotice = "Sending notice to all servers... ({0}:{1})";
        public String receivedNotice = "Received notice from server {0}:{1}, responding... ({2}:{3})";
        public String receiveRespNotice = "Received response notice from server {0}:{1}";
    }

    public static MultiServerManager instance;
    private final JavaPlugin plugin;
    private final Map<Integer, String> serverMap = new HashMap<>();

    public MultiServerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!Configuration.multiServer.enable) {
            return;
        }
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "BungeeCord", this);
        instance = this;
        serverMap.put(Configuration.multiServer.serverId, Configuration.multiServer.serverName);
        XLogger.info(Language.multiServerManagerText.sendingNotice, Configuration.multiServer.serverId, Configuration.multiServer.serverName);
        Scheduler.runTaskRepeatAsync(this::sendNotice, 0, 20 * 60 * 5);   // send notice every 5 minutes
    }

    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player  Source of the message.
     * @param message The raw message that was sent.
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!Configuration.multiServer.enable) {
            return;
        }
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (!subChannel.equals("dominion")) {
            return;
        }
        int serverId = in.readInt();
        if (serverId != Configuration.multiServer.serverId && serverId != -1) {
            return;
        }
        String action = in.readUTF();
        switch (action) {
            case "notice":
                handleNotice(in.readUTF(), in.readUTF());
                break;
            case "resp_notice":
                handleRespNotice(in.readUTF(), in.readUTF());
                break;
            case "teleport":
                handleTeleportBcMsg(in.readUTF(), in.readUTF());
                break;
        }
    }

    /**
     * Sends an action message to a specified server through the BungeeCord channel.
     * <p>
     * This method constructs a ByteArrayDataOutput object that contains the server ID,
     * action, and additional arguments, and sends it as a plugin message through the
     * BungeeCord channel.
     *
     * @param serverId The ID of the server to which the message is sent. -1 for all servers.
     * @param action   The action to be performed.
     * @param args     A list of additional arguments to be included in the message.
     * @throws IOException If an I/O error occurs while writing the data.
     */
    public void sendActionMessage(Integer serverId, String action, List<String> args) throws IOException {
        if (!Configuration.multiServer.enable) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("dominion");
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        msgout.writeInt(serverId);
        msgout.writeUTF(action);
        for (String data : args) {
            msgout.writeUTF(data);
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public void sendActionMessageAll(String action, List<String> args) throws IOException {
        sendActionMessage(-1, action, args);
    }

    public void connectToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Sends a notice to all other servers indicating that this server is loaded.
     * <p>
     * This method constructs a message containing the server ID and server name,
     * and sends it to all other servers through the BungeeCord channel.
     */
    private void sendNotice() {
        try {
            String serverId = Configuration.multiServer.serverId + "";
            String serverName = Configuration.multiServer.serverName;
            sendActionMessageAll("notice",
                    List.of(serverId, serverName)
            );
        } catch (IOException e) {
            XLogger.error(e.getMessage());
        }
    }

    /**
     * Handles the notice message received from another server.
     * <p>
     * This method processes the received server ID and server name, logs the information,
     * and sends a response notice back to the originating server.
     *
     * @param rcvServerId   The ID of the server that sent the notice.
     * @param rcvServerName The name of the server that sent the notice.
     */
    private void handleNotice(String rcvServerId, String rcvServerName) {
        try {
            String thisServerId = Configuration.multiServer.serverId + "";
            String thisServerName = Configuration.multiServer.serverName;
            XLogger.info(Language.multiServerManagerText.receivedNotice, rcvServerId, rcvServerName, thisServerId, thisServerName);
            int receivedServerId = toIntegrity(rcvServerId);
            sendActionMessage(receivedServerId, "resp_notice",
                    List.of(thisServerId, thisServerName)
            );
        } catch (Exception e) {
            XLogger.error(e.getMessage());
        }
    }

    /**
     * Handles the response notice message received from another server.
     * <p>
     * This method processes the received server ID and server name, logs the information,
     * and updates the server map with the received server information.
     *
     * @param rcvServerId   The ID of the server that sent the response notice.
     * @param rcvServerName The name of the server that sent the response notice.
     */
    private void handleRespNotice(String rcvServerId, String rcvServerName) {
        try {
            XLogger.info(Language.multiServerManagerText.receiveRespNotice, rcvServerId, rcvServerName);
            int receivedServerId = toIntegrity(rcvServerId);
            serverMap.put(receivedServerId, rcvServerName);
        } catch (Exception e) {
            XLogger.error(e.getMessage());
        }
    }

    public Map<Integer, String> getServerMap() {
        return serverMap;
    }

    public String getServerName(int serverId) {
        return serverMap.get(serverId);
    }

    public Integer getServerId(String serverName) {
        return serverMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(serverName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
