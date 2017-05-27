package net.tevp.dragon_go_countdown

import android.content.ContentResolver
import junit.framework.Assert.assertNotNull
import net.tevp.dragon_go_countdown.contentProvider.DragonContentProvider
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
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
        mContentResolver = RuntimeEnvironment.application.contentResolver
        mShadowContentResolver = Shadows.shadowOf(mContentResolver!!)
        mProvider!!.onCreate()
        ShadowContentResolver.registerProvider(DragonItemsContract.AUTHORITY, mProvider)
    }

    @Test
    fun testQuery() {
        for (uri in validUris) {
            val cursor = mShadowContentResolver!!.query(uri, null, null, null, null)
            assertNotNull(cursor)
            println(uri)
            println(cursor)
        }
    }

    companion object {
        internal val validUris = arrayOf(DragonItemsContract.Games.CONTENT_URI)
    }
}