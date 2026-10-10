package com.rama.fikret.helpers;

/**
 * The developer switch. Flip {@link #ENABLED} and rebuild; nothing is saved, so turning it off
 * takes everything it gave away back and leaves the real progress untouched.
 *
 * While it is on:
 * - every farm counts as having its guide bird (the birds follow you and the holes to the next
 *   world are open),
 * - the Teleport screen is available to travel to every world.
 */
public final class DeveloperMode {
    private DeveloperMode() {
    }

    public static final boolean ENABLED = true;
}
