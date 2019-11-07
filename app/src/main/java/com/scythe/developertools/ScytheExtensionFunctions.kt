package com.scythe.developertools

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Camilo on 6/1/2018.
 */
infix fun Int.with(x: Int) = this.or(x)

fun AppCompatActivity.setupToolbar(title: String? = null, baseView: Boolean = false,
                                   backgroundColor: Int? = null,
                                   backDrawableResId: Int? = null) {
    this.setSupportActionBar(toolbar)
    this.supportActionBar?.setDisplayShowTitleEnabled(false)
    this.supportActionBar?.setDisplayHomeAsUpEnabled(!baseView)
    if (backDrawableResId != null) {
        this.supportActionBar?.setHomeAsUpIndicator(backDrawableResId)
    }
    title?.let {
        toolbar_title.text = it
    }
    backgroundColor?.let {
        toolbar.setBackgroundColor(backgroundColor)
    }
}
