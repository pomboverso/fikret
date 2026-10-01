package com.rama.fikret.game;

public final class EnemySpawns {
    public static final class Entry {
        public final EnemyType type;
        public final int count;

        Entry(EnemyType type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    private static final Entry[] NONE = new Entry[0];

    private EnemySpawns() {
    }

    public static Entry[] forStage(int stageId) {
        switch (stageId) {
            case Maps.FOREST:
                return new Entry[]{new Entry(EnemyType.PURPLE, 3)};
            case Maps.CAVE:
                return new Entry[]{new Entry(EnemyType.PURPLE, 3), new Entry(EnemyType.BLUE, 2)};
            case Maps.VOLCANO:
                return new Entry[]{new Entry(EnemyType.RED, 3), new Entry(EnemyType.PURPLE, 2)};
            case Maps.NUCLEAR:
                return new Entry[]{new Entry(EnemyType.GREEN, 3), new Entry(EnemyType.BLUE, 2)};
            case Maps.ARCTIC:
                return new Entry[]{new Entry(EnemyType.BLUE, 4), new Entry(EnemyType.PURPLE, 2)};
            case Maps.BUBBLEGUM_LAND:
                return new Entry[]{new Entry(EnemyType.GREEN, 2), new Entry(EnemyType.RED, 2), new Entry(EnemyType.PURPLE, 2)};
            case Maps.SPACE:
                return new Entry[]{new Entry(EnemyType.RED, 3), new Entry(EnemyType.GREEN, 2), new Entry(EnemyType.BLUE, 2)};
            case Maps.NIGHTMARE:
                return new Entry[]{new Entry(EnemyType.RED, 2), new Entry(EnemyType.GREEN, 2),
                        new Entry(EnemyType.BLUE, 2), new Entry(EnemyType.PURPLE, 2)};
            default:
                return NONE;
        }
    }
}
