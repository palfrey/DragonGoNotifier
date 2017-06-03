package net.tevp.dragon_go_countdown.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.util.Log
import net.tevp.dragon_go_countdown.DragonServer
import net.tevp.dragon_go_countdown.authentication.DragonAuthenticatorActivity
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract
import net.tevp.dragon_go_countdown.contentProvider.dao.Game

class DragonSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    private val mAccountManager: AccountManager = AccountManager.get(context)

    private val TAG = "DragonSyncAdapter"

    override fun onPerformSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        Log.d(TAG, "onPerformSync for account[" + account.name + "]")
        try {
            val authToken = mAccountManager.blockingGetAuthToken(account, DragonAuthenticatorActivity.AUTHTOKEN_TYPE_FULL_ACCESS, true)
            val remoteGames = DragonServer.getGames(account.name, authToken)
            val localGames: ArrayList<Game> = ArrayList()
            val curGames = provider.query(DragonItemsContract.Games.CONTENT_URI, emptyArray(), "", emptyArray(), "")
            if (curGames != null) {
                while (curGames.moveToNext()) {
                    localGames.add(Game.fromCursor(curGames))
                }
                curGames.close()
            }

            val localGamesToRemove = ArrayList<Game>()
            for (localGame in localGames) {
                if (!remoteGames.contains(localGame))
                    localGamesToRemove.add(localGame)
            }

            Log.d(TAG, "Updating remote server with local changes")

            // Updating remote games
            for (localGame in localGames) {
                if (localGame.hasChanges()) {
                    Log.d(TAG, "Local -> Remote [$localGame]")
                    TODO("Sync game to remote")
                }
                else
                    Log.d(TAG, "$localGame has no changes")
            }

            Log.d(TAG, "Updating local database with remote changes")

            // Updating local games
            for (remoteGame in remoteGames) {
                syncResult.stats.numEntries++
                if (remoteGame in localGames) {
                    Log.d(TAG, "Remote -> Local update [$remoteGame]")
                    provider.update(remoteGame.contentUri, remoteGame.contentValues, "", emptyArray())
                    syncResult.stats.numUpdates ++
                }
                else {
                    Log.d(TAG, "Remote -> Local insert [$remoteGame]")
                    provider.insert(DragonItemsContract.Games.CONTENT_URI, remoteGame.contentValues)
                    syncResult.stats.numInserts ++
                }
                context.contentResolver.notifyChange(remoteGame.contentUri, null, false)
            }

            for (localGame in localGamesToRemove) {
                Log.d(TAG, "Removing $localGame from local storage")
                provider.delete(localGame.contentUri, "", emptyArray())
                context.contentResolver.notifyChange(localGame.contentUri, null, false)
                syncResult.stats.numDeletes ++
            }

            Log.d(TAG, syncResult.stats.toString())
            Log.d(TAG, "Finished")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}