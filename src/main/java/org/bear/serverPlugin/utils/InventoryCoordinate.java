package org.bear.serverPlugin.utils;

public interface InventoryCoordinate {
    int getX();
    int getY();

    default int getSlotIndex() {
        return getY() * 9 + getX();
    }

    default boolean isAt(InventoryCoordinate coordinate) {
        return getSlotIndex() == coordinate.getSlotIndex();
    }
}
