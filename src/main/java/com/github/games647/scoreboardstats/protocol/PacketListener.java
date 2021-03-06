package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases.
 * This Listener should only read and listen to relevant packets.
 */
public class PacketListener extends PacketAdapter {

    //Shorter access
    private static final PacketType DISPLAY_TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
    private static final PacketType OBJECTIVE_TYPE = PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
    private static final PacketType SCORE_TYPE = PacketType.Play.Server.SCOREBOARD_SCORE;

    private final PacketSbManager manager;

    /**
     * Creates a new packet listener
     *
     * @param plugin plugin for registrating into protocollib
     * @param manager packetmanager instance
     */
    public PacketListener(Plugin plugin, PacketSbManager manager) {
        super(plugin, DISPLAY_TYPE, OBJECTIVE_TYPE, SCORE_TYPE);

        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PacketType packetType = event.getPacketType();
        if (packetType.equals(SCORE_TYPE)) {
            handleScorePacket(event);
        } else if (packetType.equals(OBJECTIVE_TYPE)) {
            handleObjectivePacket(event);
        } else if (packetType.equals(DISPLAY_TYPE)) {
            handleDisplayPacket(event);
        }
    }

    private void handleScorePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String scoreName = packet.getStrings().read(0);
        final String parent = packet.getStrings().read(1);
        final int score = packet.getIntegers().read(0);
        final State action = State.fromId(packet.getIntegers().read(1));

        //Packet receiving validation
        if (scoreName.length() > 16 || action == null || (action == State.CREATED && parent.length() > 16)) {
            //Invalid packet
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (action == State.CREATED) {
            scoreboard.createOrUpdateScore(scoreName, parent, score);
        } else if (action == State.REMOVED) {
            scoreboard.resetScore(scoreName);
        }
    }

    private void handleObjectivePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String objectiveName = packet.getStrings().read(0);
        //Can be empty
        final String displayName = packet.getStrings().read(1);
        final State action = State.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (action == null || objectiveName.length() > 16 || displayName.length() > 32) {
            //Invalid packet
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);

        if (action == State.CREATED) {
            scoreboard.addObjective(objectiveName, displayName);
        } else {
            final Objective objective = scoreboard.getObjective(objectiveName);
            if (objective == null) {
                //Could cause a NPE at the client if the objective wasn't found
                return;
            }

            if (action == State.REMOVED) {
                scoreboard.removeObjective(objectiveName);
            } else if (action == State.UPDATE_TITLE) {
                objective.setDisplayName(displayName, false);
            }
        }
    }

    private void handleDisplayPacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        //Can be empty; if so it would just clear the slot
        final String objectiveName = packet.getStrings().read(0);
        final Slot slot = Slot.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (slot == null || objectiveName.length() > 16) {
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);

        if (slot == Slot.SIDEBAR) {
            scoreboard.setSidebarObjective(objectiveName);
        } else {
            final Objective sidebarObjective = scoreboard.getSidebarObjective();
            if (sidebarObjective != null && sidebarObjective.getName().equals(objectiveName)) {
                scoreboard.clearSidebarObjective();
            }
        }
    }
}
