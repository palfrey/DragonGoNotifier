package net.tevp.dragon_go_countdown.contentProvider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DragonItemsContract {
    public static final String AUTHORITY = "net.tevp.dragon_go_countdown";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Games implements BaseColumns {
        public static String OPPONENT_HANDLE = "OPPONENT_HANDLE";
        public static String PLAYER_COLOR = "PLAYER_COLOR";
        public static String LASTMOVE_DATE = "LASTMOVE_DATE";
        public static String TIME_REMAINING = "TIME_REMAINING";
        public static String GAME_ACTION = "GAME_ACTION";
        public static String GAME_STATUS = "GAME_STATUS";
        public static String MOVE_ID = "MOVE_ID";
        public static String TOURNAMENT_ID = "TOURNAMENT_ID";
        public static String SHAPE_ID = "SHAPE_ID";
        public static String GAME_TYPE = "GAME_TYPE";
        public static String GAME_PRIO = "GAME_PRIO";
        public static String OPPONENT_LASTACCESS_DATE = "OPPONENT_LASTACCESS_DATE";
        public static String HANDICAP = "HANDICAP";
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =  Uri.withAppendedPath(DragonItemsContract.CONTENT_URI, "games");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.net.tevp.dragon_go_countdown.games";
        /**
         * A projection of all columns in the items table.
         */
        /*public static final String[] PROJECTION_ALL = {
                _ID,
                OPPONENT_HANDLE,
                PLAYER_COLOR,
                LASTMOVE_DATE,

        };*/

        public static final String SORT_ORDER_DEFAULT = _ID + " ASC";
    }

}
