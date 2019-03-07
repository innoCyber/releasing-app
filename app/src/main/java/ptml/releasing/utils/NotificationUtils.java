package ptml.releasing.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;


import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import ptml.releasing.R;

public class NotificationUtils {

    public static void notifyUser(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void notifyUser(View view, String message){
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
       /* FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) snackBar.getView().getLayoutParams();*/
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBar.getView();
        snackBarLayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
        ((TextView)snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text))
                .setTextColor(view.getContext().getResources().getColor(android.R.color.white));
        /*snackBar.getView().setLayoutParams(param);*/
        snackBar.show();
    }



   /* @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannelIfNotExist(NotificationManager notificationManager, String title) {
        if (notificationManager.getNotificationChannel(CHANNEL_ONE_ID) == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    title, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }*/

}
