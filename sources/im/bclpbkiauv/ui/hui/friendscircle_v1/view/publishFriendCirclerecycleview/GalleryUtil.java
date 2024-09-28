package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import com.king.zxing.util.LogUtils;

public class GalleryUtil {
    public static String getRealPathFromUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            return getRealPathFromUriAboveApi19(context, uri);
        }
        return getRealPathFromUriBelowAPI19(context, uri);
    }

    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, (String) null, (String[]) null);
    }

    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                return getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_id=?", new String[]{documentId.split(LogUtils.COLON)[1]});
            } else if (isDownloadsDocument(uri)) {
                return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId).longValue()), (String) null, (String[]) null);
            } else {
                return null;
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, (String) null, (String[]) null);
        } else {
            if ("file".equals(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {"_data"};
        Cursor cursor = null;
        try {
            Cursor cursor2 = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String) null);
            if (cursor2 == null || !cursor2.moveToFirst()) {
                return null;
            }
            return cursor2.getString(cursor2.getColumnIndexOrThrow(projection[0]));
        } catch (Exception e) {
            if (cursor == null) {
                return null;
            }
            cursor.close();
            return null;
        }
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
