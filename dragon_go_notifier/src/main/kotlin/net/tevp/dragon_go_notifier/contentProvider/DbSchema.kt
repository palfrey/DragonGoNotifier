package net.tevp.dragon_go_notifier.contentProvider

object DbSchema {
    val DB_NAME = "dragonitems.db"

    object Games {
        const val TBL_NAME = "games"
        const val COL_ID = DragonItemsContract.Games._ID
        const val COL_USERNAME = DragonItemsContract.Games.USERNAME
        const val COL_OPPONENT_HANDLE = DragonItemsContract.Games.OPPONENT_HANDLE
        const val COL_END_TIME = DragonItemsContract.Games.END_TIME
        const val COL_MY_TURN = DragonItemsContract.Games.MY_TURN

        val DDL_CREATE_TBL = "CREATE TABLE $TBL_NAME (" +
                "$COL_ID           INTEGER  PRIMARY KEY, \n" +
                "$COL_USERNAME     VARCHAR(64), \n" +
                "$COL_OPPONENT_HANDLE     TEXT,\n" +
                "$COL_END_TIME      DATETIME, \n" +
                "$COL_MY_TURN      BOOLEAN \n" +
                ")"
    }

    object Widgets {
        const val TBL_NAME = "widgets"
        const val COL_ID = DragonItemsContract.Widgets._ID
        const val COL_USERNAME = DragonItemsContract.Widgets.USERNAME

        val DDL_CREATE_TBL = "CREATE TABLE $TBL_NAME (" +
                "$COL_ID           INTEGER  PRIMARY KEY, \n" +
                "$COL_USERNAME     VARCHAR(64) \n" +
                ")"
    }

    object Users {
        const val TBL_NAME = "users"
        const val COL_USERNAME = DragonItemsContract.Users.USERNAME
        const val COL_HOLIDAY_HOURS = DragonItemsContract.Users.HOLIDAY_HOURS

        val DDL_CREATE_TBL = "CREATE TABLE $TBL_NAME (" +
                "$COL_USERNAME     VARCHAR(64) PRIMARY KEY, \n" +
                "$COL_HOLIDAY_HOURS     INTEGER \n" +
                ")"
    }
}
