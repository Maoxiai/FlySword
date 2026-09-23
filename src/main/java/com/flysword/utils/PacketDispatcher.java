package com.flysword.utils;

import com.flysword.FlySwordMod;
import com.flysword.network.server.SpawnSwordBeamPacket;
import com.flysword.network.server.SwordSkillPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketDispatcher {
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(FlySwordMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    /**
     * Registers all packets and handlers - call this during mod construction
     */
    public static void initialize() {
        int packetId = 0;
        CHANNEL.messageBuilder(SpawnSwordBeamPacket.class, packetId++)
                .encoder(SpawnSwordBeamPacket::toBytes)
                .decoder(SpawnSwordBeamPacket::new)
                .consumerMainThread(SpawnSwordBeamPacket::handle)
                .add();
        CHANNEL.messageBuilder(SwordSkillPacket.class, packetId++)
                .encoder(SwordSkillPacket::toBytes)
                .decoder(SwordSkillPacket::new)
                .consumerMainThread(SwordSkillPacket::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}
