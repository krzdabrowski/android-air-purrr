package com.example.trubul.airpurrr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

class CustomDialog {
    private Context mContext;
    private ChangeListener mListener;
    private String newStringAutoThreshold;
    private int newIntAutoThreshold;
    private boolean isCorrectInput;

    interface ChangeListener {
        void onChange();
    }

    int getThreshold() {
        return newIntAutoThreshold;
    }

    void setListener(ChangeListener listener) {
        mListener = listener;
    }

    CustomDialog(Context context) {
        mContext = context;
    }

    // TODO: inflate dialog from xml layout new file instead on making everything in Java code (lecture 255 on Udemy)
    void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        TextView title = new TextView(mContext);
        final EditText editText = new EditText(mContext);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextSize(32);
        editText.post(() -> {  // show keyboard automatically
            editText.requestFocusFromTouch();
            InputMethodManager lManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            lManager.showSoftInput(editText, 0);
        });

        title.setText(R.string.menu_enter_a_value);
        title.setPadding(16, 16, 16, 16);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(24);

        builder.setCustomTitle(title);
        builder.setCancelable(true);  // with BACK button
        builder.setPositiveButton(R.string.menu_ok, (dialog, id) -> {});
        builder.setNegativeButton(R.string.menu_cancel, (dialog, id) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        float dpi = mContext.getResources().getDisplayMetrics().density;
        dialog.setView(editText, (int) (135 * dpi), (int) (10 * dpi), (int) (135 * dpi), (int) (10 * dpi));
        dialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((view) -> {
            newStringAutoThreshold = editText.getText().toString();
            getAutoThreshold();

            if (isCorrectInput)
                dialog.dismiss();
        });
    }

    private void getAutoThreshold() {
        if (!TextUtils.isEmpty(newStringAutoThreshold)) {
            isCorrectInput = true;
            newIntAutoThreshold = Integer.parseInt(newStringAutoThreshold);
            mListener.onChange();
        }
        else {
            isCorrectInput = false;
            Toast.makeText(mContext, R.string.menu_enter_the_correct_value, Toast.LENGTH_SHORT).show();
        }
    }

}
