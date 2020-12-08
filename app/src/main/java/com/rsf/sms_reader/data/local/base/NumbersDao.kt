package com.rsf.sms_reader.data.local.base

import androidx.room.*
import io.reactivex.Single

const val NUMBERS_TABLE = "NUMBERS_TABLE"
@Dao
interface NumbersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(entity: NumbersEntity): Single<Unit>

    @Query("SELECT * FROM $NUMBERS_TABLE")
    fun read(): Single<List<NumbersEntity>>

    @Update
    fun update(entity: NumbersEntity): Single<Unit>

    @Delete
    fun delete(entity: NumbersEntity): Single<Unit>
}