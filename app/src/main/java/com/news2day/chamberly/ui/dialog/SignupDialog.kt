package com.news2day.chamberly.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputEditText
import com.news2day.chamberly.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

class SignupDialog(context:Context):Dialog(context) {

    private val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_email_pass_signup, null)
    private val dialog: AlertDialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

     val dialogButtonSignup: Button = dialogView.findViewById(R.id.btnSignup)
    private val name:TextInputEditText = dialogView.findViewById(R.id.etSignupNameText)
    private val email:TextInputEditText = dialogView.findViewById(R.id.etSignupEmailText)
    private val password:TextInputEditText = dialogView.findViewById(R.id.etSignupPasswordText)
    val progressBar: ProgressBar = dialogView.findViewById<ProgressBar>(R.id.pbSignup)

    init {
        dialogButtonSignup.setOnClickListener {
            if (name.text.isNullOrEmpty() || email.text.isNullOrEmpty() || password.text.isNullOrEmpty()){
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                onSignupButtonClicked(name.text.toString(), email.text.toString(), password.text.toString())
            }
        }

        Objects.requireNonNull<Window>(this.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.dialog_background_inset
            )
        )
        this.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun show() {
        dialog.show()
    }

    var onSignupButtonClicked: (name:String, email:String, password:String) -> Unit =
        { name, email, password ->
        }
}