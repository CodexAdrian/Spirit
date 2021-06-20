package me.codexadrian.spirit.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class EventHandler {

    public static int ticks = 0;
    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> ticks++);
    }
}
