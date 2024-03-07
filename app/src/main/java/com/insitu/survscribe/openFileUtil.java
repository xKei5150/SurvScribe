package com.insitu.survscribe;

import android.content.Context;
import android.content.Intent;

public class openFileUtil {
    public static void startDCPTestLayoutActivity(Context context, String filename) {
        Intent intent = new Intent(context, dcp_test_layout.class);
        intent.putExtra("filename", filename);
        context.startActivity(intent);
    }

    public static void startFDTestLayoutActivity(Context context, String filename) {
        Intent intent = new Intent(context, fd_test_layout.class);
        intent.putExtra("filename", filename);
        context.startActivity(intent);
    }

    public static void startSPTestLayoutActivity(Context context, String filename) {
        Intent intent = new Intent(context, sp_test_layout.class);
        intent.putExtra("filename", filename);
        context.startActivity(intent);
    }

}
