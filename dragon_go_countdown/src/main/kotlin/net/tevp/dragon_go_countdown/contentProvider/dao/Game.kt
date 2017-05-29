package net.tevp.dragon_go_countdown.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import net.tevp.dragon_go_countdown.contentProvider.DbSchema

import java.io.Serializable

class Game(var opponent_handle: String, var borrower: String) : Serializable {
    val contentValues: ContentValues
        get() {
            val values = ContentValues()
            values.put(DbSchema.COL_OPPONENT_HANDLE, opponent_handle)
            values.put(DbSchema.COL_BORROWER, borrower)
            return values
        }

    companion object {
        fun fromCursor(curGames: Cursor): Game {
            val opponent_handle = curGames.getString(curGames.getColumnIndex(DbSchema.COL_OPPONENT_HANDLE))
            val borrower = curGames.getString(curGames.getColumnIndex(DbSchema.COL_BORROWER))

            return Game(opponent_handle, borrower)
        }
    }
}