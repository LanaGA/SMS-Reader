package com.rsf.sms_reader.di

import androidx.room.Room
import com.rsf.sms_reader.data.local.NumbersRepository
import com.rsf.sms_reader.data.local.NumbersRepositoryImpl
import com.rsf.sms_reader.data.local.base.NumbersDao
import com.rsf.sms_reader.data.local.base.NumbersDatabase
import com.rsf.sms_reader.data.local.base.NUMBERS_TABLE
import com.rsf.sms_reader.data.remote.service.SMSReceiver
import com.rsf.sms_reader.data.remote.service.ServiceGenerator
import com.rsf.sms_reader.ui.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val roomModule = module {

    single<NumbersDatabase> {
        Room.databaseBuilder(
                androidContext(),
                NumbersDatabase::class.java,
                NUMBERS_TABLE
        ).build()
    }

    single<NumbersDao> {
        get<NumbersDatabase>().numbersDao()
    }

    single<NumbersRepository> {
        NumbersRepositoryImpl(get())
    }

    viewModel {
        ViewModel(get())
    }
}