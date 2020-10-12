package com.example.imageconverter.mvp.model

import android.graphics.Bitmap

interface IImageLoader {
    fun getImage(src: String): Bitmap
}