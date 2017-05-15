package net.tevp.dragon_go_countdown.contentProvider

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

object DragonItemsContract {
    val AUTHORITY = "net.tevp.dragon_go_countdown"
    val CONTENT_URI = Uri.parse("content://" + AUTHORITY)

    class Games : BaseColumns {
        companion object {
            var _ID = BaseColumns._ID;
            var OPPONENT_HANDLE = "OPPONENT_HANDLE"
            var PLAYER_COLOR = "PLAYER_COLOR"
            var LASTMOVE_DATE = "LASTMOVE_DATE"
            var TIME_REMAINING = "TIME_REMAINING"
            var GAME_ACTION = "GAME_ACTION"
            var GAME_STATUS = "GAME_STATUS"
            var MOVE_ID = "MOVE_ID"
            var TOURNAMENT_ID = "TOURNAMENT_ID"
            var SHAPE_ID = "SHAPE_ID"
            var GAME_TYPE = "GAME_TYPE"
            var GAME_PRIO = "GAME_PRIO"
            var OPPONENT_LASTACCESS_DATE = "OPPONENT_LASTACCESS_DATE"
            var HANDICAP = "HANDICAP"
            /**
             * The content URI for this table.
             */
            val CONTENT_URI = Uri.withAppendedPath(DragonItemsContract.CONTENT_URI, "games")
            /**
             * The mime type of a directory of items.
             */
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games"
            /**
             * The mime type of a single item.
             */
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games"
            /**
             * A projection of all columns in the items table.
             */
            /*public static final String[] PROJECTION_ALL = {
                _ID,
                OPPONENT_HANDLE,
                PLAYER_COLOR,
                LASTMOVE_DATE,

        };*/

            val SORT_ORDER_DEFAULT = BaseColumns._ID + " ASC"
        }
    }

}
