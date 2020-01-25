package net.tevp.dragon_go_notifier

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ProviderInfo
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert.assertNotNull
import net.tevp.dragon_go_notifier.contentProvider.DragonContentProvider
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowContentResolver


@RunWith(RobolectricTestRunner::class)
class DragonContentProviderTest {
    private var mContentResolver: ContentResolver? = null
    private var mShadowContentResolver: ShadowContentResolver? = null
    private var mProvider: DragonContentProvider? = null

    @Before
    fun setup() {
        mProvider = DragonContentProvider()
        mContentResolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        mShadowContentResolver = Shadows.shadowOf(mContentResolver!!)
        mProvider!!.onCreate()
        val info = ProviderInfo()
        info.authority = DragonItemsContract.AUTHORITY
        Robolectric.buildContentProvider(DragonContentProvider::class.java).create(info)
    }

    @Test
    fun testQuery() {
        for (uri in validUris) {
            val cursor = mContentResolver!!.query(uri, emptyArray(), "", emptyArray(), "")
            assertNotNull(cursor)
            println(uri)
            println(cursor)
        }
    }

    companion object {
        internal val validUris = arrayOf(DragonItemsContract.Games.CONTENT_URI)
    }
}