package com.gronsfeldcipher.app

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_enter_password.view.*
import kotlinx.android.synthetic.main.dialog_incorrect_password.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var latin: CharArray
    private lateinit var cyrillic: CharArray
    private lateinit var mixed: CharArray
    private lateinit var key: CharArray

    private var maxPassEntry = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latin = this.resources.getString(R.string.latin).toCharArray()
        cyrillic = this.resources.getString(R.string.cyrillic).toCharArray()
        mixed = latin + cyrillic

        key = this.resources.getString(R.string.default_key).toCharArray()

        txtEncoded.movementMethod = ScrollingMovementMethod()
        txtDecoded.movementMethod = ScrollingMovementMethod()

        showPasswordDialog()

        btnEncode.setOnClickListener {
            if (fKey.text.isNullOrBlank() || fText.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            if (fKey.text!!.length > fText.text!!.length) {
                Toast.makeText(
                    this,
                    "Довжина ключена не повинна перевищувати довжину тексту",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            mixed = mixed.toList().shuffled().toCharArray()
            txtDecoded.text = ""
            key = fKey.text.toString().toCharArray()
            txtEncoded.text = encodeText(fText.text.toString())
        }

        btnDecode.setOnClickListener {
            if (fKey.text.isNullOrBlank() || fText.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            if (fKey.text!!.length > fText.text!!.length) {
                Toast.makeText(
                    this,
                    "Довжина ключена не повинна перевищувати довжину тексту",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (txtEncoded.text.isNullOrBlank()) {
                key = fKey.text.toString().toCharArray()
                txtEncoded.text = encodeText(fText.text.toString())
            }

            txtDecoded.text = decodeText(txtEncoded.text.toString())
        }

    }

    private fun encodeText(text: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val pos = if (i + key.size > text.length) text.length else i + key.size
            val sub_text = text.substring(i, pos)
            sb.append(cipherSub(key, sub_text, 1))
            i += key.size
        }
        return sb.toString()
    }

    private fun decodeText(text: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val pos = if (i + key.size > text.length) text.length else i + key.size
            val subText = text.substring(i, pos)
            sb.append(cipherSub(key, subText, -1))
            i += key.size
        }
        return sb.toString()
    }

    private fun cipherSub(key: CharArray, subt: String, sign: Int): String {
        val sb = StringBuilder()
        assert(subt.length <= key.size)
        for (i in subt.indices) {
            sb.append(getCipherChar(key.toList()[i].toString().toInt() * sign, subt[i]))
        }

        return sb.toString()
    }

    private fun getCipherChar(k: Int, p_char: Char): Char {
        val pPos = mixed.asList().indexOf(p_char)

        if (pPos == -1) {
            return p_char
        }

        var result = when (k + pPos) {
            in Int.MIN_VALUE..0 -> mixed.size + (k + pPos)
            in mixed.size..Int.MAX_VALUE -> k + pPos - mixed.size
            else -> k + pPos
        }

        if (result == mixed.size) result = mixed.size - 1

        return mixed[result]
    }

    private fun showPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_password, null)
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()
        alertDialog.setCanceledOnTouchOutside(false)
        with(dialogView) {
            btnCheck.setOnClickListener {
                if (fPassword.text.isNullOrBlank()) {
                    return@setOnClickListener
                }

                if (!isValidPassword(fPassword.text.toString())) {
                    if (maxPassEntry > 0) {
                        showInvalidPasswordAlert()
                    } else {
                        showBlockedAlert()
                    }
                    alertDialog.dismiss()
                    return@setOnClickListener
                }
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun showInvalidPasswordAlert() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_incorrect_password, null)
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()
        alertDialog.setCanceledOnTouchOutside(false)
        with(dialogView) {

            invalid_password_desc_txv.text =
                getString(R.string.invalid_pass_alert, maxPassEntry)

            btnOK.setOnClickListener {
                maxPassEntry--
                alertDialog.dismiss()
                showPasswordDialog()
            }
        }
        alertDialog.show()
    }


    private fun showBlockedAlert() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_blocked, null)
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length in 10..12
    }

}
