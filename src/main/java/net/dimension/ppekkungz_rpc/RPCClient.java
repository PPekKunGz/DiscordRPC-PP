package net.dimension.ppekkungz_rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import com.mojang.authlib.GameProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
//            URL url = new URL("http://localhost:3232/rpc/init"); //todo: can use https://example.com/rpc/init
            URL url = new URL("https://launcher.xn--12cgj3ga1lya4d6c.xn--o3cw4h/rpc/init"); //todo: can use https://example.com/rpc/init
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

            JsonObject jsonResponse = JsonParser.parseString(content.toString()).getAsJsonObject();
            String applicationId = jsonResponse.get("applicationId").getAsString();
            boolean autoRegister = jsonResponse.get("autoRegister").getAsBoolean();
            String steamId = jsonResponse.get("steamId").getAsString();
            String smallImageKey = jsonResponse.get("smallImageKey").getAsString();
            String details = jsonResponse.get("details").getAsString();
            String defaultState = jsonResponse.get("state").getAsString();
            boolean useUsername = jsonResponse.get("useUsername").getAsBoolean();

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
