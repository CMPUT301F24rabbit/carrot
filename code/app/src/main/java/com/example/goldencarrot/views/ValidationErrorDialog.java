package com.example.goldencarrot.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ValidationErrorDialog {

    /**
     * Displays a validation error dialog with an input message
     *
     * @param context The context class
     * @param title   Dialog title
     * @param message Error message to be displayed
     */
    public static void show(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
