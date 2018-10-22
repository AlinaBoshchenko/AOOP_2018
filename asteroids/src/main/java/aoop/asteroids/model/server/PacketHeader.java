package aoop.asteroids.model.server;

import java.io.Serializable;

public enum PacketHeader implements Serializable {
    PACKET_LOGIN,
    PACKET_SPECTATE,
    PACKET_DISCONNECT,
    PACKET_ACCEPTED,
    PACKET_DENIED;
}
