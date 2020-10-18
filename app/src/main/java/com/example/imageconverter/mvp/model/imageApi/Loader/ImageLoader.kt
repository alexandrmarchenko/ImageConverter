package com.example.imageconverter.mvp.model.imageApi.Loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.example.imageconverter.mvp.model.imageApi.IImageLoader
import io.reactivex.rxjava3.core.Observable

class ImageLoader(private val context: Context) :
    IImageLoader {

    private fun getImage(src: String): Bitmap {
        val src = Uri.parse(src)
        val source = ImageDecoder.createSource(context.contentResolver, src)
        return ImageDecoder.decodeBitmap(source)
    }

    override fun loadImage(src: String): Observable<Bitmap> =
        Observable.fromCallable {
            val image = getImage(src)
            return@fromCallable image
        }

}