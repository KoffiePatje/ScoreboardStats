package com.github.games647.scoreboardstats;

/**
 * Tracks how many ticks are passed per second.
 */
public class TicksPerSecondTask implements Runnable {

    private static double lastTicks = 20.0D;

    /**
     * Get the ticks count of the last check.
     *
     * @return the ticks count of the last check
     */
    public static double getLastTicks() {
        return lastTicks;
    }

    //the last time we updated the ticks
    private long lastCheck;

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();
        final long difference = currentTime - lastCheck;
        lastCheck = currentTime;

        final double tps = 20 * 1000.0D / difference;
        if (tps >= 0.0D && tps < 25.0D) {
            //Prevent all invalid values
            lastTicks = tps;
        }
    }
}
