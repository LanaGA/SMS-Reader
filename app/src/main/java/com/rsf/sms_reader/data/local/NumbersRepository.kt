package com.rsf.sms_reader.data.local

import com.rsf.sms_reader.data.local.base.NumbersEntity
import io.reactivex.Single

interface NumbersRepository {
    fun create(entity: NumbersEntity): Single<Unit>

    fun update(entity: NumbersEntity): Single<Unit>

    fun delete(entity: NumbersEntity): Single<Unit>

    fun getAll(): Single<List<NumbersEntity>>
}