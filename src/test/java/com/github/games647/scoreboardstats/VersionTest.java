package com.github.games647.scoreboardstats;

import org.bukkit.Bukkit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(Bukkit.class)
@RunWith(PowerMockRunner.class)
public class VersionTest {

    @Test
    public void testParsing() {
        Version version = new Version(1, 2, 3);

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 2, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 3, version.getBuild());

        version = new Version("3.2.1");

        Assert.assertEquals("Major version exception: " + version, 3, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 2, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 1, version.getBuild());

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getVersion()).thenReturn("git-Bukkit-1.5.2-R1.0-1-gf46bd58-b2793jnks (MC: 1.7.9)");
        version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 7, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 9, version.getBuild());


        Mockito.when(Bukkit.getVersion()).thenReturn("git-Spigot-1439 (MC: 1.7.9)");
        version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 7, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 9, version.getBuild());


        Mockito.when(Bukkit.getVersion()).thenReturn("git-SportBukkit-1.7.2-R0.3-100-gace9685  (MC: 1.7.9)");
        version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 7, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 9, version.getBuild());
    }

    /**
     * Test of compareTo method, of class Version.
     */
    @Test
    public void testComparison() {
        final Version low = new Version(1, 5, 4);
        final Version high = new Version(1, 8, 5);

        Assert.assertSame("Higher Compare: " + high + ' ' + low, high.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + high, low.compareTo(high), -1);

        final Version higher = new Version(1, 5, 5);
        Assert.assertSame("Higher Compare: " + higher + ' ' + low, higher.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + higher, low.compareTo(higher), -1);

        final Version equal = new Version(1, 2, 3);
        final Version equal1 = new Version(1, 2, 3);
        Assert.assertSame("Equal Compare: " + equal + ' ' + equal1, equal.compareTo(equal1), 0);
        Assert.assertSame("Equal Compare: " + equal1 + ' ' + equal, equal1.compareTo(equal1), 0);

        Assert.assertEquals("Equals: " + equal + ' ' + equal1, equal, equal1);
        Assert.assertEquals("Equals: " + equal1 + ' ' + equal,equal1, equal);
    }
}
