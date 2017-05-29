package net.tevp.dragon_go_countdown.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
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
            val remoteGames = DragonServer.getGames(authToken)
            val localGames: ArrayList<Game> = ArrayList()
            val curGames = provider.query(DragonItemsContract.CONTENT_URI, null, null, null, null)
            if (curGames != null) {
                while (curGames.moveToNext()) {
                    localGames.add(Game.fromCursor(curGames))
                }
                curGames.close()
            }

            val gamesToRemote = ArrayList<Game>()
            for (localGame in localGames) {
                if (!remoteGames.contains(localGame))
                    gamesToRemote.add(localGame)
            }

            val gamesToLocal = ArrayList<Game>()
            for (remoteGame in remoteGames) {
                if (!localGames.contains(remoteGame))
                    gamesToLocal.add(remoteGame)
            }

            if (gamesToRemote.isEmpty()) {
                Log.d(TAG, "No local changes to update server")
            } else {
                Log.d(TAG, "Updating remote server with local changes")

                // Updating remote games
                for (remoteGame in gamesToRemote) {
                    Log.d(TAG, "Local -> Remote [" + remoteGame.opponent_handle + "]")
                    TODO("Sync game to remote")
                    //parseComService.putShow(authToken, userObjectId, remoteGame)
                }
            }

            if (gamesToLocal.isEmpty()) {
                Log.d(TAG, "No server changes to update local database")
            } else {
                Log.d(TAG, "Updating local database with remote changes")

                // Updating local games
                val showsToLocalValues = arrayOfNulls<ContentValues>(gamesToLocal.size)
                for ((i, localGame) in gamesToLocal.withIndex()) {
                    Log.d(TAG, "Remote -> Local [" + localGame.opponent_handle + "]")
                    showsToLocalValues[i] = localGame.contentValues
                }
                provider.bulkInsert(DragonItemsContract.CONTENT_URI, showsToLocalValues)
            }

            Log.d(TAG, "Finished")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}