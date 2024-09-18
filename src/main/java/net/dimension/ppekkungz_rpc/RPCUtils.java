package net.dimension.ppekkungz_rpc;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCUtils implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("RPCUtils");

    @Override
    public void onInitialize() {
        LOGGER.info("RPCUtils Start..");
    }
}
