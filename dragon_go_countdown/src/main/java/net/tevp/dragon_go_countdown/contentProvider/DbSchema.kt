package net.tevp.dragon_go_countdown.contentProvider

interface DbSchema {
    companion object {
        val DB_NAME = "dragonitems.db"

        val TBL_GAMES = "games"

        val DDL_CREATE_TBL_GAMES = "CREATE TABLE games (" +
                "_id           INTEGER  PRIMARY KEY AUTOINCREMENT, \n" +
                "opponent_handle     TEXT,\n" +
                "borrower      TEXT \n" +
                ")"
        val DDL_DROP_TBL_GAMES = "DROP TABLE IF EXISTS games"
    }
}
