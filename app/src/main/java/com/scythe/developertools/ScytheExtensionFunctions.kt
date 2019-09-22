package com.scythe.developertools

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Camilo on 6/1/2018.
 */
infix fun Int.with(x: Int) = this.or(x)

fun AppCompatActivity.setupToolbar(title: String, baseView: Boolean = false) {
    this.setSupportActionBar(toolbar)
    this.supportActionBar?.setDisplayShowTitleEnabled(false)
    this.supportActionBar?.setDisplayHomeAsUpEnabled(!baseView)
    toolbar_title.text = title
}
