package com.sunmi.sunmit2demo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * author : chrc
 * date   : 2019/7/6  5:32 PM
 * desc   :
 */
public class ToastUtil {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static Toast toast;

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(Context context, @NonNull final CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    private static void show(final Context context, final CharSequence text, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                toast = Toast.makeText(context, text, duration);
//                toast = new Toast(context);
//                toast.setView(createView(text));
//                toast.setDuration(duration);
//                if (gravity != -1 || xOffset != -1 || yOffset != -1) {
//                    toast.setGravity(gravity, xOffset, yOffset);
//                }
                toast.show();
            }
        });
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
