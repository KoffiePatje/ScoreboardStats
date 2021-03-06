package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates the specific packets and send it with help of ProtocolLib.
 */
public final class PacketFactory {

    private static final int SIDEBAR_SLOT = 1;

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    /**
     * Sends a new scoreboard item packet.
     *
     * @param item the scoreboard item
     * @param state whether the item should be send as removed or created/updated
     */
    public static void sendPacket(Item item, State state) {
        final PacketContainer scorePacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        scorePacket.getStrings().write(0, item.getScoreName());

        if (State.REMOVED != state) {
            //Weird minecraft
            scorePacket.getStrings().write(1, item.getParent().getName());
            scorePacket.getIntegers().write(0, item.getScore());
        }

        scorePacket.getIntegers().write(1, state.ordinal());

        try {
            PROTOCOL_MANAGER.sendServerPacket(item.getOwner(), scorePacket, false);
        } catch (InvocationTargetException ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends a new scoreboard objective packet and a display packet (if the
     * objective was created)
     *
     * @param objective the scoreboard objective
     * @param state whether the objective was created, updated (displayname) or removed
     */
    public static void sendPacket(Objective objective, State state) {
        final PacketContainer objectivePacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        objectivePacket.getStrings().write(0, objective.getName());

        if (state != State.REMOVED) {
            objectivePacket.getStrings().write(1, objective.getDisplayName());
        }

        objectivePacket.getIntegers().write(0, state.ordinal());

        try {
            PROTOCOL_MANAGER.sendServerPacket(objective.getOwner(), objectivePacket, false);
            sendDisplayPacket(objective);
        } catch (InvocationTargetException ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the sidebar slot to this objective
     *
     * @param objective the displayed objective, if getName() is empty it will just clear the sidebar
     */
    public static void sendDisplayPacket(Objective objective) {
        final PacketContainer displayPacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        //Can be empty
        displayPacket.getStrings().write(0, objective.getName());

        displayPacket.getIntegers().write(0, SIDEBAR_SLOT);

        try {
            PROTOCOL_MANAGER.sendServerPacket(objective.getOwner(), displayPacket, false);
        } catch (InvocationTargetException ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    private PacketFactory() {
        //Utility class
    }
}
