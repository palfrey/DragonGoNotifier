package net.tevp.dragon_go_notifier.contentProvider.dao

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import net.tevp.dragon_go_notifier.contentProvider.DbSchema
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract
import java.io.Serializable

class Widget(var widget_id: Int, var username: String) : Serializable {
    val contentValues: ContentValues
        get() {
            val values = ContentValues()
            values.put(DbSchema.Widgets.COL_ID, widget_id)
            values.put(DbSchema.Widgets.COL_USERNAME, username)
            return values
        }

    val contentUri: Uri
        get() {
            return Uri.withAppendedPath(DragonItemsContract.Widgets.CONTENT_URI, this.widget_id.toString())
        }

    override fun toString(): String {
        return "Widget[ID: $widget_id, Username: $username]"
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass.isInstance(other)) {
            val otherGame = other as Widget
            return this.widget_id == otherGame.widget_id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int{
        var result = widget_id
        result = 31 * result + username.hashCode()
        return result
    }

    companion object {
        fun fromCursor(curWidgets: Cursor): Widget {
            val widget_id = curWidgets.getInt(curWidgets.getColumnIndex(DbSchema.Widgets.COL_ID))
            val username = curWidgets.getString(curWidgets.getColumnIndex(DbSchema.Widgets.COL_USERNAME))

            return Widget(widget_id, username)
        }
    }
}