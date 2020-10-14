package com.example.imageconverter.mvp.model.imageApi.Converter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.imageconverter.mvp.model.entity.Image
import com.example.imageconverter.mvp.model.imageApi.IConverter
import io.reactivex.rxjava3.core.Completable

class Converter(private val context: Context) :
    IConverter {

    val TAG: String = Converter::class.java.simpleName

    override fun convert(image: Image): Completable {
        return Completable.fromAction {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e(TAG, e.message ?: "Sleep error")
            }

            val bitmap = image.bitmap
            val trg = Uri.parse(image.trg)

            try {
                bitmap?.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    context.contentResolver.openOutputStream(trg)
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Conversion error")
            }

        }
    }

}