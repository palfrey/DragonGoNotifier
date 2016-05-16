package net.tevp.dragon_go_countdown.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract.Games;

import java.sql.SQLException;

public class DragonContentProvider extends ContentProvider {
    private static final int GAME_LIST = 1;
    private static final int GAME_ID = 2;
    private static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(DragonItemsContract.AUTHORITY,
                "games",
                GAME_LIST);
        URI_MATCHER.addURI(DragonItemsContract.AUTHORITY,
                "games/#",
                GAME_ID);
    }

    private DragonItemsOpenHelper mHelper = null;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();
    @Override
    public boolean onCreate() {
        mHelper = new DragonItemsOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            case GAME_LIST:
                builder.setTables(DbSchema.TBL_GAMES);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Games.SORT_ORDER_DEFAULT;
                }
                break;
            case GAME_ID:
                builder.setTables(DbSchema.TBL_GAMES);
                // limit query to one row at most:
                builder.appendWhere(Games._ID + " = " +
                        uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case GAME_LIST:
                return Games.CONTENT_TYPE;
            case GAME_ID:
                return Games.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != GAME_LIST) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id =
                db.insert(
                        DbSchema.TBL_GAMES,
                        null,
                        values);
        try {
            return getUriForId(id, uri);
        } catch (SQLException e) {
            Log.e("DragonContentProvider", e.toString());
            return null;
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    private Uri getUriForId(long id, Uri uri) throws SQLException {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case GAME_LIST:
                delCount = db.delete(
                        DbSchema.TBL_GAMES,
                        selection,
                        selectionArgs);
                break;
            case GAME_ID:
                String idStr = uri.getLastPathSegment();
                String where = Games._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DbSchema.TBL_GAMES,
                        where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount;
        switch (URI_MATCHER.match(uri)) {
            case GAME_LIST:
                updateCount = db.update(
                        DbSchema.TBL_GAMES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case GAME_ID:
                String idStr = uri.getLastPathSegment();
                String where = Games._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DbSchema.TBL_GAMES,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}
