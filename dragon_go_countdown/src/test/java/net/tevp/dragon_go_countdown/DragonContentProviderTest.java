package net.tevp.dragon_go_countdown;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import net.tevp.dragon_go_countdown.contentProvider.DragonContentProvider;
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowContentResolver;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class DragonContentProviderTest{
    private ContentResolver mContentResolver;
    private ShadowContentResolver mShadowContentResolver;
    private DragonContentProvider mProvider;

    static final Uri[] validUris = new Uri[] {DragonItemsContract.Games.CONTENT_URI};

    @Before
    public void setup() {
        mProvider = new DragonContentProvider();
        mContentResolver = RuntimeEnvironment.application.getContentResolver();
        mShadowContentResolver = Shadows.shadowOf(mContentResolver);
        mProvider.onCreate();
        ShadowContentResolver.registerProvider(DragonItemsContract.AUTHORITY, mProvider);
    }

    @Test
    public void testQuery() {
        for (Uri uri : validUris) {
            Cursor cursor = mShadowContentResolver.query(uri, null, null, null, null);
            assertNotNull(cursor);
            System.out.println(uri);
            System.out.println(cursor);
        }
    }
}