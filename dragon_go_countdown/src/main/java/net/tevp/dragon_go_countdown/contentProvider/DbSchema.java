package net.tevp.dragon_go_countdown.contentProvider;

public interface DbSchema {
    String DB_NAME = "dragonitems.db";

    String TBL_GAMES = "games";

    String DDL_CREATE_TBL_GAMES = "CREATE TABLE games (" +
            "_id           INTEGER  PRIMARY KEY AUTOINCREMENT, \n" +
            "opponent_handle     TEXT,\n" +
            "borrower      TEXT \n" +
            ")";;
    String DDL_DROP_TBL_GAMES = "DROP TABLE IF EXISTS games";
}
