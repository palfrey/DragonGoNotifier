package net.tevp.dragon_go_countdown.contentProvider

object DbSchema {
    val DB_NAME = "dragonitems.db"

    const val TBL_NAME = "games"
    const val COL_ID = "_id"
    const val COL_OPPONENT_HANDLE = "opponent_handle"
    const val COL_BORROWER = "borrower"

    val DDL_CREATE_TBL_GAMES = "CREATE TABLE games (" +
            "_id           INTEGER  PRIMARY KEY AUTOINCREMENT, \n" +
            "opponent_handle     TEXT,\n" +
            "borrower      TEXT \n" +
            ")"
    val DDL_DROP_TBL_GAMES = "DROP TABLE IF EXISTS games"
}
