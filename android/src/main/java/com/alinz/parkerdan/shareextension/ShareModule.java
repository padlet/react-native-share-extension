package com.alinz.parkerdan.shareextension;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.graphics.Bitmap;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.R.attr.value;


public class ShareModule extends ReactContextBaseJavaModule {


    public ShareModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "ReactNativeShareExtension";
    }

    @ReactMethod
    public void close() {
        if (getCurrentActivity() != null) {
            getCurrentActivity().finish();
        }
    }

    @ReactMethod
    public void data(Promise promise) {
        promise.resolve(processIntent());
    }

    public WritableMap processIntent() {
        WritableMap map = Arguments.createMap();

        List<String> values = new ArrayList<>();
        String type = "";
        String action = "";

        Activity currentActivity = getCurrentActivity();

        if (currentActivity != null) {
            Intent intent = currentActivity.getIntent();
            action = intent.getAction();
            type = intent.getType();
            if (type == null) {
                type = "";
            }
            if (Intent.ACTION_SEND.equals(action) && !type.equals("")) {
                if ("text/plain".equals(type)) {
                    values.add(handleSendText(intent)); // Handle text being sent
                } else {
                    values.add(handleSendFile(intent)); // Handle single file being sent
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && !type.equals("")) {
                values.addAll(handleSendMultipleFiles(intent)); // Handle single image being sent

            } else {
                // not ACTION_SEND or ACTION_SEND_MULTIPLE (will not happen)
                // do nothing
            }
        } else {
            // null activity
            // do nothing
        }

        WritableArray files = Arguments.createArray();
        for (String value : values) {
            WritableMap file = Arguments.createMap();
            file.putString("type", type);
            file.putString("value", value);
            files.pushMap(file);
        }
        map.putArray("files", files);

        return map;
    }

    private Collection<? extends String> handleSendMultipleFiles(Intent intent) {
        List<String> filePaths= new ArrayList<>();
        List<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            for (Uri uri: uris) {
                filePaths.add("file://" + getRealPath(uri));
            }
        }
        return filePaths;
    }


    private String handleSendFile(Intent intent) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        return "file://" + getRealPath(uri);
    }

    private String handleSendText(Intent intent) {
        return intent.getStringExtra(Intent.EXTRA_TEXT);
    }

    private String getRealPath(Uri uri) {
        String result = RealPathUtilV2.getRealPathFromURI(getCurrentActivity(), uri);
        return result != null ? result : RealPathUtil.getRealPathFromURI(getCurrentActivity(), uri);
    }
}
