package net.dimension.ppekkungz_rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import com.mojang.authlib.GameProfile;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RPCClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("RPCClient");

    @Override
    public void onInitializeClient() {
        try {
            URL url = new URL("http://localhost:3232/rpc/init"); //todo: can use https://example.com/rpc/init
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject jsonResponse = new JSONObject(content.toString());
            String applicationId = jsonResponse.getString("applicationId");
            boolean autoRegister = jsonResponse.getBoolean("autoRegister");
            String steamId = jsonResponse.getString("steamId");
            String smallImageKey = jsonResponse.getString("smallImageKey");
            String details = jsonResponse.getString("details");
            String defaultState = jsonResponse.getString("state");
            boolean useUsername = jsonResponse.getBoolean("useUsername");

            Minecraft client = Minecraft.getInstance();
            GameProfile profile = client.getUser().getGameProfile();
            String username = profile.getName();

            String state;
            if (useUsername) {
                state = "Minecraft ID: " + username;
            } else {
                state = defaultState;
            }

            DiscordRPC lib = DiscordRPC.INSTANCE;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            lib.Discord_Initialize(applicationId, handlers, autoRegister, steamId);

            DiscordRichPresence presence = new DiscordRichPresence();
            presence.instance = 1;
            presence.smallImageKey = smallImageKey;
            presence.details = details;
            presence.state = state;
            presence.startTimestamp = System.currentTimeMillis() / 1000;

            lib.Discord_UpdatePresence(presence);

            LOGGER.info("RPCClient Start..");

        } catch (Exception e) {
            LOGGER.error("Failed to initialize Discord RPC", e);
        }
    }
}
