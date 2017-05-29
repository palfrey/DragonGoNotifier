package net.tevp.dragon_go_countdown.contentProvider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract.Games
import java.sql.SQLException

class DragonContentProvider : ContentProvider() {

    private var mHelper: DragonItemsOpenHelper? = null
    private val mIsInBatchMode = ThreadLocal<Boolean>()
    override fun onCreate(): Boolean {
        mHelper = DragonItemsOpenHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String): Cursor {
        var localSortOrder = sortOrder
        val db = mHelper!!.readableDatabase
        val builder = SQLiteQueryBuilder()
        when (URI_MATCHER.match(uri)) {
            GAME_LIST -> {
                builder.tables = DbSchema.TBL_NAME
                if (TextUtils.isEmpty(sortOrder)) {
                    localSortOrder = Games.SORT_ORDER_DEFAULT
                }
            }
            GAME_ID -> {
                builder.tables = DbSchema.TBL_NAME
                // limit query to one row at most:
                builder.appendWhere(Games._ID + " = " +
                        uri.lastPathSegment)
            }
            else -> throw IllegalArgumentException(
                    "Unsupported URI: " + uri)
        }
        val cursor = builder.query(
                db,
                projection,
                selection,
                selectionArgs, null, null,
                localSortOrder)
        // if we want to be notified of any changes:
        cursor.setNotificationUri(
                context.contentResolver,
                uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        when (URI_MATCHER.match(uri)) {
            GAME_LIST -> return Games.CONTENT_TYPE
            GAME_ID -> return Games.CONTENT_ITEM_TYPE
            else -> return null
        }
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        if (URI_MATCHER.match(uri) != GAME_LIST) {
            throw IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri)
        }
        val db = mHelper!!.writableDatabase
        val id = db.insert(
                DbSchema.TBL_NAME, null,
                values)
        try {
            return getUriForId(id, uri)
        } catch (e: SQLException) {
            Log.e("DragonContentProvider", e.toString())
            return null
        }

    }

    private val isInBatchMode: Boolean
        get() = mIsInBatchMode.get() != null && mIsInBatchMode.get()

    @Throws(SQLException::class)
    private fun getUriForId(id: Long, uri: Uri): Uri {
        if (id > 0) {
            val itemUri = ContentUris.withAppendedId(uri, id)
            if (!isInBatchMode) {
                // notify all listeners of changes:
                context.contentResolver.notifyChange(itemUri, null)
            }
            return itemUri
        }
        // s.th. went wrong:
        throw SQLException(
                "Problem while inserting into uri: " + uri)
    }

    override fun delete(uri: Uri, selection: String, selectionArgs: Array<String>): Int {
        val db = mHelper!!.writableDatabase
        var delCount: Int
        when (URI_MATCHER.match(uri)) {
            GAME_LIST -> delCount = db.delete(
                    DbSchema.TBL_NAME,
                    selection,
                    selectionArgs)
            GAME_ID -> {
                val idStr = uri.lastPathSegment
                var where = Games._ID + " = " + idStr
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection
                }
                delCount = db.delete(
                        DbSchema.TBL_NAME,
                        where,
                        selectionArgs)
            }
            else -> throw IllegalArgumentException("Unsupported URI: " + uri)
        }
        // notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode) {
            context.contentResolver.notifyChange(uri, null)
        }
        return delCount
    }

    override fun update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val db = mHelper!!.writableDatabase
        val updateCount: Int
        when (URI_MATCHER.match(uri)) {
            GAME_LIST -> updateCount = db.update(
                    DbSchema.TBL_NAME,
                    values,
                    selection,
                    selectionArgs)
            GAME_ID -> {
                val idStr = uri.lastPathSegment
                var where = Games._ID + " = " + idStr
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection
                }
                updateCount = db.update(
                        DbSchema.TBL_NAME,
                        values,
                        where,
                        selectionArgs)
            }
            else ->
                // no support for updating photos or entities!
                throw IllegalArgumentException("Unsupported URI: " + uri)
        }
        // notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode) {
            context.contentResolver.notifyChange(uri, null)
        }
        return updateCount
    }

    companion object {
        private val GAME_LIST = 1
        private val GAME_ID = 2
        private val URI_MATCHER: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        // prepare the UriMatcher
        init {
            URI_MATCHER.addURI(DragonItemsContract.AUTHORITY,
                    "games",
                    GAME_LIST)
            URI_MATCHER.addURI(DragonItemsContract.AUTHORITY,
                    "games/#",
                    GAME_ID)
        }
    }
}
