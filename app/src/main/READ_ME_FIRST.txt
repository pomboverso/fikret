This is the full current state of the game/ package (all 14 files) -
matches your new map-format spec. Drop this over app/src/main/java/com/
rama/fikret/game/ in your project (nothing outside game/ needed to change
for this update).

NEW FILES
  ItemType.java - registry matching your "Items" list (Nothing, Stone,
                   Hole Down, Hole Up, Bird), each with its drawable and
                   whether it blocks movement.
  Bird.java     - the companion: stands still until the goose steps onto
                   its tile, then follows one tile behind from then on.

REWRITTEN FOR THE NEW EEDDCBBA FORMAT
  MapCell.java  - now decodes the full 8-digit code: item (E), background
                   tile+direction (D+C), tile+direction (B+A). Values with
                   fewer digits are left-padded with zeros first, so short
                   codes still work (e.g. 15 -> grass, centered, nothing
                   else - see the worked examples in the file header).
  TileType.java - expanded to all 17 biomes from your list (ids match
                   exactly), plus NONE(0) for "no background layer here".
  GameMap.java  - added isPassable(row, col): false if out of bounds OR
                   the cell's item blocks movement (stone does, nothing
                   else does yet).
  Goose.java    - one-line change: now checks isPassable(), not just
                   isInBounds(), before stepping onto a tile.
  GameView.java - draws background layer, then tile layer, then any
                   static item (stone/hole/hole_up) on top; spawns a Bird
                   if the map has an ItemType.BIRD cell; drives the
                   follow-one-tile-behind logic each frame; the on-screen
                   direction arrows and Goose's own movement both now
                   respect isPassable() (a stone gets no arrow pointing
                   at it and can't be walked into).
  Maps.java     - MEADOW rebuilt in the new format (all cells are now
                   8-digit-equivalent values), with one stone and one
                   bird placed a few tiles from the goose's spawn point
                   so you can walk straight into both to test them.

HOW THE STONE AND BIRD WORK RIGHT NOW
  - Stone: fully static, just blocks the tile it's on. Nothing else -
    no picking it up, no pushing it. That's item id 01, blocksMovement
    = true in ItemType.java if you want to look at how it's wired.
  - Bird: sits still until the goose walks onto its exact tile, then
    tags along one tile behind for the rest of the stage (through
    GameView.updateBird()). It doesn't do anything else yet - no way to
    "drop" it, no separate behavior once following.

ONE THING TO DOUBLE-CHECK ON YOUR END
  Your "Stone on top of grass" example used item code 02 (which is Hole
  Down in your Items list, not Stone which is 01) - I assumed that was a
  typo and used 01 for Stone everywhere. Flag it if it wasn't.

NOT DONE YET (didn't seem to be asked for, but flagging so it's a
conscious choice rather than an oversight)
  - Hole Down / Hole Up have art wired up (they'll render as static images
    if you place them on a map) but no actual teleport/hole behavior.
  - Only one bird per stage (findBirdSpawn() stops at the first one it
    finds) - say the word if you want multiple.
  - No animation/feedback on the stone when the goose bumps into it -
    it just silently doesn't move.
