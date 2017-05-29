package net.tevp.dragon_go_countdown.contentProvider

object DbSchema {
    val DB_NAME = "dragonitems.db"

    const val TBL_NAME = "games"
    const val COL_ID = "id"
    const val COL_OPPONENT_HANDLE = "opponent_handle"
    const val COL_END_TIME = "end_time"

    val DDL_CREATE_TBL_GAMES = "CREATE TABLE $TBL_NAME (" +
            "$COL_ID           INTEGER  PRIMARY KEY, \n" +
            "$COL_OPPONENT_HANDLE     TEXT,\n" +
            "$COL_END_TIME      DATETIME \n" +
            ")"
    val DDL_DROP_TBL_GAMES = "DROP TABLE IF EXISTS $TBL_NAME"
}
