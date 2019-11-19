/**
 * RealPathUtilV2
 * Created by Padlet on 11/18/19
 * Colin Teahan
 */

package com.alinz.parkerdan.shareextension;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Real Path Util V2
 * This is our own custom build real path utility which doesn't rely on content resolvers and media
 * queries to retrieve sharable content. Instead this class will create a temporary file in the users
 * cache documents directory and then return the link to the newly created file. This seems to work on
 * all android versions for downloads, media, Google drive, etc.
 *
 * The downside to doing it this way is that extra time is spent copying the file locally and then
 * uploading the copy, which for large video files may take twice as long. However, this method
 * seems to be far more robust and easier to maintain.
 */
public class RealPathUtilV2 {

    private final static String TAG = "PadletShareExtension";
    private final static String SUB_DIRECTORY = "documents";

    @SuppressLint("NewApi")
    public static @Nullable String getRealPathFromURI(@NonNull final Context context,
                                                      @NonNull final Uri uri) {
        return createFileInTempPath(context, uri);
    }

    /**
     * This method will move the file at the given uri to the temporary file path that we create, if
     * the ../cache/documents directory doesn't already exist we create one, then creste a new file,
     * and then move the file to the new directory. The method will return null if this fails, in
     * which case we will want to default back to the original RealPathUtil.
     */
    public static @Nullable String createFileInTempPath(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        File cacheDir = new File(context.getCacheDir(), SUB_DIRECTORY);
        if (!cacheDir.exists()) cacheDir.mkdirs();
        File file = new File(cacheDir, fileName);
        try {
            file.createNewFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        String destinationPath = null;
        if (file != null) {
            destinationPath = file.getAbsolutePath();
            saveFileFromUri(context, uri, destinationPath);
        }
        return destinationPath;
    }

    private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            bos = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
            byte[] buf = new byte[1024];
            is.read(buf);
            do {
                bos.write(buf);
            } while (is.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null && context != null) {
            String path = getPath(context, uri);
            if (path == null) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null,
                    null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }

        return filename;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }

    public static String getPath(final Context context, final Uri uri) {
        String absolutePath = getRealPathFromURI(context, uri);
        return absolutePath != null ? absolutePath : uri.toString();
    }
}
