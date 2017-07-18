package net.tevp.dragon_go_notifier.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import net.tevp.dragon_go_notifier.contentProvider.DbSchema
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract
import java.io.Serializable

class User(var username: String, var holiday_hours: Int) : Serializable {
    val contentValues: ContentValues
        get() {
            val values = ContentValues()
            values.put(DbSchema.Users.COL_USERNAME, username)
            values.put(DbSchema.Users.COL_HOLIDAY_HOURS, holiday_hours)
            return values
        }

    val contentUri: Uri
        get() {
            return Uri.withAppendedPath(DragonItemsContract.Users.CONTENT_URI, this.username)
        }

    override fun toString(): String {
        return "User[Username: $username, Holiday hours: $holiday_hours]"
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass.isInstance(other)) {
            val otherGame = other as User
            return this.username == otherGame.username
        }
        return super.equals(other)
    }

    override fun hashCode(): Int{
        val result = username.hashCode()
        return result
    }

    companion object {
        fun fromCursor(curUsers: Cursor): User {
            val username = curUsers.getString(curUsers.getColumnIndex(DbSchema.Users.COL_USERNAME))
            val holiday_hours = curUsers.getInt(curUsers.getColumnIndex(DbSchema.Users.COL_HOLIDAY_HOURS))
            return User(username, holiday_hours)
        }
    }
}