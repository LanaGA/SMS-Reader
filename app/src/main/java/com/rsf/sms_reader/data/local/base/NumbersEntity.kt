package com.rsf.sms_reader.data.local.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NUMBERS_TABLE)
data class NumbersEntity(
        @PrimaryKey
        @ColumnInfo(name = "number")
        val number: String
)