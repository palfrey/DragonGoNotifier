package net.tevp.dragon_go_countdown.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import net.tevp.dragon_go_countdown.contentProvider.DbSchema
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract

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

    val contentUri: Uri
        get() {
            return Uri.withAppendedPath(DragonItemsContract.Games.CONTENT_URI, this.game_id.toString())
        }

    fun hasChanges(): Boolean = false

    override fun toString(): String {
        return "Game[ID: $game_id, Opponent: $opponent_handle, End time: $end_time]"
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass.isInstance(other)) {
            val otherGame = other as Game
            return this.game_id == otherGame.game_id
        }
        return super.equals(other)
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