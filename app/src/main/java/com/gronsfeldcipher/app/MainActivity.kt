package com.gronsfeldcipher.app

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var latin: CharArray
    private lateinit var cyrillic: CharArray
    private lateinit var mixed: CharArray
    private lateinit var key: CharArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latin = this.resources.getString(R.string.latin).toCharArray()
        cyrillic = this.resources.getString(R.string.cyrillic).toCharArray()
        mixed = latin + cyrillic
        mixed = mixed.toList().shuffled().toCharArray()

        key = this.resources.getString(R.string.default_key).toCharArray()

        txtEncoded.movementMethod = ScrollingMovementMethod()
        txtDecoded.movementMethod = ScrollingMovementMethod()

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

        val result = when (k + pPos) {
            in Int.MIN_VALUE..0 -> mixed.size + (k + pPos)
            in mixed.size..Int.MAX_VALUE -> k + pPos - mixed.size
            else -> k + pPos
        }

        return mixed[result]
    }

}
