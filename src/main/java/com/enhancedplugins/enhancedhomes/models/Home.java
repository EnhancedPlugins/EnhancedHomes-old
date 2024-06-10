package com.enhancedplugins.enhancedhomes.models;

/**
 * The Home class represents a home of a player.
 * It contains the name of the home, the name of the world in which the home is located, and the coordinates of the home.
 */
public class Home {
    private String name;
    private String worldName;
    private double x;
    private double y;
    private double z;

    /**
     * Constructor for the Home class.
     *
     * @param name The name of the home.
     * @param worldName The name of the world in which the home is located.
     * @param x The X coordinate of the home.
     * @param y The Y coordinate of the home.
     * @param z The Z coordinate of the home.
     */
    public Home(String name, String worldName, double x, double y, double z) {
        this.name = name;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieves the name of the home.
     *
     * @return The name of the home.
     */
    public String getName() { return name; }

    /**
     * Retrieves the name of the world in which the home is located.
     *
     * @return The name of the world.
     */
    public String getWorldName() { return worldName; }

    /**
     * Retrieves the X coordinate of the home.
     *
     * @return The X coordinate.
     */
    public double getX() { return x; }

    /**
     * Retrieves the Y coordinate of the home.
     *
     * @return The Y coordinate.
     */
    public double getY() { return y; }

    /**
     * Retrieves the Z coordinate of the home.
     *
     * @return The Z coordinate.
     */
    public double getZ() { return z; }

    /**
     * Sets the name of the home.
     *
     * @param name The new name of the home.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the name of the world in which the home is located.
     *
     * @param worldName The new name of the world.
     */
    public void setWorldName(String worldName) { this.worldName = worldName; }

    /**
     * Sets the X coordinate of the home.
     *
     * @param x The new X coordinate.
     */
    public void setX(double x) { this.x = x; }

    /**
     * Sets the Y coordinate of the home.
     *
     * @param y The new Y coordinate.
     */
    public void setY(double y) { this.y = y; }

    /**
     * Sets the Z coordinate of the home.
     *
     * @param z The new Z coordinate.
     */
    public void setZ(double z) { this.z = z; }
}