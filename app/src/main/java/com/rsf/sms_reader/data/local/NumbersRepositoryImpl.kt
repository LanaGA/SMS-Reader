package com.rsf.sms_reader.data.local

import com.rsf.sms_reader.data.local.base.NumbersDao
import com.rsf.sms_reader.data.local.base.NumbersEntity
import io.reactivex.Single

class NumbersRepositoryImpl(private val NumbersDao: NumbersDao) : NumbersRepository {
    override fun create(entity: NumbersEntity): Single<Unit> =
        NumbersDao.create(entity)

    override fun update(entity: NumbersEntity): Single<Unit> =
        NumbersDao.update(entity)

    override fun delete(entity: NumbersEntity): Single<Unit> =
        NumbersDao.delete(entity)

    override fun getAll(): Single<List<NumbersEntity>> =
        NumbersDao.read()
}