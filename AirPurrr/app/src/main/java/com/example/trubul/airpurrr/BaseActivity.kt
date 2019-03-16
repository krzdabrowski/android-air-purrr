package com.example.trubul.airpurrr

import android.app.ProgressDialog

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null

    internal fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.login_message_dialog))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    internal fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

}
