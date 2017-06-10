package net.tevp.dragon_go_countdown.widget

import android.accounts.AccountManager
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import net.tevp.dragon_go_countdown.R
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract

class DragonWidgetConfigure : Activity() {
    @BindView(R.id.accountsList) lateinit var mAccountsList: Spinner

    val TAG = "DragonWidgetConfigure"

    private lateinit var adapter: ArrayAdapter<String>

    private fun setAccounts() {
        adapter.clear()
        for (a in AccountManager.get(baseContext).getAccountsByType(DragonItemsContract.AUTHORITY)) {
            Log.d(TAG, "Account: $a")
            adapter.add(a.name)
        }
    }

    private var mAppWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)
        adapter = ArrayAdapter<String>(baseContext, android.R.layout.simple_spinner_item)
        ButterKnife.bind(this)

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(Activity.RESULT_CANCELED, resultValue)
        }

        setAccounts()
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAccountsList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        setAccounts()
    }

    fun addAccount(@Suppress("UNUSED_PARAMETER") view: View)
    {
        startActivity(Intent(Settings.ACTION_ADD_ACCOUNT))
    }

    fun saveSettings(@Suppress("UNUSED_PARAMETER") view: View) {
        val appWidgetManager = AppWidgetManager.getInstance(baseContext)
        val views = RemoteViews(baseContext.packageName, R.layout.widget)
        appWidgetManager.updateAppWidget(mAppWidgetId, views)

        val options = Bundle()
        options.putString(DragonWidgetContract.USERNAME, mAccountsList.selectedItem as String)
        appWidgetManager.updateAppWidgetOptions(mAppWidgetId, options)

        val updateIntent = Intent(this.applicationContext,
        DragonWidgetProvider::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mAppWidgetId))
        sendBroadcast(updateIntent)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
