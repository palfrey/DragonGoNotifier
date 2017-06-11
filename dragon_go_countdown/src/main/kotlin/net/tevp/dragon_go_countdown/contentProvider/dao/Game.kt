package net.tevp.dragon_go_countdown.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import net.tevp.dragon_go_countdown.contentProvider.DbSchema
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract

import java.io.Serializable
import java.util.*

class Game(var game_id: Int, var username: String, var opponent_handle: String, var end_time: Date) : Serializable {
    val contentValues: ContentValues
        get() {
            val values = ContentValues()
            values.put(DbSchema.Games.COL_ID, game_id)
            values.put(DbSchema.Games.COL_USERNAME, username)
            values.put(DbSchema.Games.COL_OPPONENT_HANDLE, opponent_handle)
            values.put(DbSchema.Games.COL_END_TIME, end_time.time)
            return values
        }

    val contentUri: Uri
        get() {
            return Uri.withAppendedPath(DragonItemsContract.Games.CONTENT_URI, this.game_id.toString())
        }

    fun hasChanges(): Boolean = false

    override fun toString(): String {
        return "Game[ID: $game_id, Username: $username Opponent: $opponent_handle, End time: $end_time]"
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass.isInstance(other)) {
            val otherGame = other as Game
            return this.game_id == otherGame.game_id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int{
        var result = game_id
        result = 31 * result + username.hashCode()
        result = 31 * result + opponent_handle.hashCode()
        result = 31 * result + end_time.hashCode()
        return result
    }

    companion object {
        fun fromCursor(curGames: Cursor): Game {
            val game_id = curGames.getInt(curGames.getColumnIndex(DbSchema.Games.COL_ID))
            val username = curGames.getString(curGames.getColumnIndex(DbSchema.Games.COL_USERNAME))
            val opponent_handle = curGames.getString(curGames.getColumnIndex(DbSchema.Games.COL_OPPONENT_HANDLE))
            val end_time_raw = curGames.getLong(curGames.getColumnIndex(DbSchema.Games.COL_END_TIME))

            return Game(game_id, username, opponent_handle, Date(end_time_raw))
        }
    }
}