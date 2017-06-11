package net.tevp.dragon_go_countdown.contentProvider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DragonItemsOpenHelper(context: Context) : SQLiteOpenHelper(context, DragonItemsOpenHelper.NAME, null, DragonItemsOpenHelper.VERSION) {
    val TAG = "DragonItemsOpenHelper";

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbSchema.Games.DDL_CREATE_TBL)
        db.execSQL(DbSchema.Widgets.DDL_CREATE_TBL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this is no sample for how to handle SQLite databases
        // thus I simply drop and recreate the database here.
        //
        // NEVER do this in real apps. Your users wouldn't like
        // loosing data just because you decided to change the schema
        db.execSQL(DbSchema.Games.DDL_DROP_TBL)
        db.execSQL(DbSchema.Widgets.DDL_DROP_TBL)
        onCreate(db)
    }

    companion object {
        private val NAME = DbSchema.DB_NAME
        private val VERSION = 1
    }
}
