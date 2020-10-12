package com.example.imageconverter.mvp.presenter

import android.content.Context
import android.graphics.Bitmap
import com.example.imageconverter.mvp.model.entity.Converter
import com.example.imageconverter.mvp.model.entity.Image
import com.example.imageconverter.mvp.model.entity.ImageLoader
import com.example.imageconverter.mvp.view.ImageView
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import moxy.MvpPresenter

class Presenter(
    private val context: Context,
    private val schedulers: Scheduler,
    private val computationScheduler: Scheduler
) : MvpPresenter<ImageView>() {

    private val image = Image()

    var converterDisposable: Disposable = Disposable.empty()

    private val converter = Converter(context)
    private val imageLoader = ImageLoader(context)

    fun setTarget(trg: String) {
        image.trg = trg
    }

    fun convert() {
        viewState.showConvertDialog()

        image.bitmap?.let {
            image.trg?.let {
                converter.convert(image)
                    .subscribeOn(computationScheduler)
                    .observeOn(schedulers)
                    .doOnSubscribe { d -> converterDisposable = d }
                    .subscribe({
                        viewState.dismissConvertDialog()
                        viewState.showConvertSuccess()
                    }, {
                        viewState.dismissConvertDialog()
                        viewState.showConvertFail()
                    })
            }
        }
    }

    fun loadImage(src: String) {
        var bitmap: Bitmap? = null
        imageLoader.loadImage(src)
            .subscribeOn(computationScheduler)
            .observeOn(schedulers)
            .subscribe { t ->
                image.bitmap = t
                viewState.setImage(t)
            }

    }

    fun stopConverting() {
        if (!converterDisposable.isDisposed) {
            converterDisposable.dispose()
            viewState.dismissConvertDialog()
        }
    }
}