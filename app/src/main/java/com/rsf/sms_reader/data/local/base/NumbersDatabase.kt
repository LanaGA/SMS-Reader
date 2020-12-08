package com.rsf.sms_reader.data.local.base

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NumbersEntity::class], version = 2)
abstract class NumbersDatabase : RoomDatabase() {
    abstract fun numbersDao(): NumbersDao
}