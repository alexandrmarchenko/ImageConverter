package com.example.imageconverter.mvp.model.imageApi

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Observable

interface IImageLoader {
    fun loadImage(src: String): Observable<Bitmap>
}