package com.example.imageconverter.mvp.model.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.example.imageconverter.mvp.model.IImageLoader

class ImageLoader(private val context: Context) : IImageLoader {
    override fun getImage(src: String): Bitmap {
        val src = Uri.parse(src)
        val source = ImageDecoder.createSource(context.contentResolver, src)
        val imageBitmap = ImageDecoder.decodeBitmap(source)
        return imageBitmap
    }
}