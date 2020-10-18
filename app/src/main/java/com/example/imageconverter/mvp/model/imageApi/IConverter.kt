package com.example.imageconverter.mvp.model.imageApi

import com.example.imageconverter.mvp.model.entity.Image
import io.reactivex.rxjava3.core.Completable

interface IConverter {
    fun convert(image: Image): Completable
}