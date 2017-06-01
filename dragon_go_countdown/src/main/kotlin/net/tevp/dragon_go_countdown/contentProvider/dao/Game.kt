package net.tevp.dragon_go_countdown.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import net.tevp.dragon_go_countdown.contentProvider.DbSchema

import java.io.Serializable
import java.util.*

class Game(var game_id: Int, var opponent_handle: String, var end_time: Date) : Serializable {
    val contentValues: ContentValues
        get() {
            val values = ContentValues()
            values.put(DbSchema.COL_ID, game_id)
            values.put(DbSchema.COL_OPPONENT_HANDLE, opponent_handle)
            values.put(DbSchema.COL_END_TIME, end_time.time)
            return values
        }

    override fun toString(): String {
        return "Game[ID: $game_id, Opponent: $opponent_handle, End time: $end_time]"
    }

    companion object {
        fun fromCursor(curGames: Cursor): Game {
            val game_id = curGames.getInt(curGames.getColumnIndex(DbSchema.COL_ID))
            val opponent_handle = curGames.getString(curGames.getColumnIndex(DbSchema.COL_OPPONENT_HANDLE))
            val end_time_raw = curGames.getLong(curGames.getColumnIndex(DbSchema.COL_END_TIME))

            return Game(game_id, opponent_handle, Date(end_time_raw))
        }
    }
}