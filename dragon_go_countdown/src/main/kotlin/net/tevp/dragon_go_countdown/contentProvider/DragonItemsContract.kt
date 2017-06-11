package net.tevp.dragon_go_countdown.contentProvider

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

object DragonItemsContract {
    val AUTHORITY = "net.tevp.dragon_go_countdown"
    val CONTENT_URI = Uri.parse("content://" + AUTHORITY)!!

    class Games : BaseColumns {
        companion object {
            const val _ID: String = BaseColumns._ID
            const val OPPONENT_HANDLE = "OPPONENT_HANDLE"
            const val END_TIME = "END_TIME"
            const val USERNAME = "USERNAME"

            /**
             * The content URI for this table.
             */
            val CONTENT_URI = Uri.withAppendedPath(DragonItemsContract.CONTENT_URI, "games")!!
            /**
             * The mime type of a directory of items.
             */
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games"
            /**
             * The mime type of a single item.
             */
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games"

            val SORT_ORDER_DEFAULT = _ID + " ASC"
        }
    }

    class Widgets : BaseColumns {
        companion object {
            const val _ID: String = BaseColumns._ID
            const val USERNAME = "USERNAME"

            /**
             * The content URI for this table.
             */
            val CONTENT_URI = Uri.withAppendedPath(DragonItemsContract.CONTENT_URI, "widgets")!!
            /**
             * The mime type of a directory of items.
             */
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.widgets"
            /**
             * The mime type of a single item.
             */
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.widgets"
            val SORT_ORDER_DEFAULT = _ID + " ASC"
        }
    }
}
