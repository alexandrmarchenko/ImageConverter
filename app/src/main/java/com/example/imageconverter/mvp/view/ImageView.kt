package com.example.imageconverter.mvp.view

import android.graphics.Bitmap
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface ImageView : MvpView {
    fun chooseImage()
    fun convertImage()
    fun showConvertDialog()
    fun dismissConvertDialog()
    fun showConvertFail()
    fun showConvertSuccess()
    fun setImage(bitmap: Bitmap)
}