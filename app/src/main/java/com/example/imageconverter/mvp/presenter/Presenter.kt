package com.example.imageconverter.mvp.presenter

import com.example.imageconverter.mvp.model.entity.Image
import com.example.imageconverter.mvp.model.imageApi.Converter.Converter
import com.example.imageconverter.mvp.model.imageApi.Loader.ImageLoader
import com.example.imageconverter.mvp.view.ImageView
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter

class Presenter(
    private val schedulers: Scheduler,
    private val computationScheduler: Scheduler,
    private val converter: Converter,
    private val imageLoader: ImageLoader
) : MvpPresenter<ImageView>() {

    private val image = Image()

    var converterDisposable: Disposable = Disposable.empty()

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