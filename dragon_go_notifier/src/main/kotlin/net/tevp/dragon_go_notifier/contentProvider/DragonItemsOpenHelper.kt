package net.tevp.dragon_go_notifier.contentProvider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DragonItemsOpenHelper(context: Context) : SQLiteOpenHelper(context, DragonItemsOpenHelper.NAME, null, DragonItemsOpenHelper.VERSION) {
    val TAG = "DragonItemsOpenHelper"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbSchema.Games.DDL_CREATE_TBL)
        db.execSQL(DbSchema.Widgets.DDL_CREATE_TBL)
        db.execSQL(DbSchema.Users.DDL_CREATE_TBL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.i(TAG, "Upgrade db: $oldVersion -> $newVersion")
        if (oldVersion < 3) {
            db.execSQL(DbSchema.Users.DDL_CREATE_TBL)
        }
    }

    companion object {
        private val NAME = DbSchema.DB_NAME
        private val VERSION = 3
    }
}
