package com.rsf.sms_reader.ui

import com.rsf.sms_reader.base.Event
import com.rsf.sms_reader.data.local.base.NumbersEntity

const val ADDRESS = "ADDRESS"
const val MY_SETTINGS = "MY_SETTINGS"

data class ViewState(
        val status: STATUS,
        val numbersList: List<NumbersEntity>
)

sealed class UiEvent : Event {
    data class CreateNumber(
            val numbersModel: NumbersEntity
    ) : UiEvent()

    data class DeleteNumber(val numbersModel: NumbersEntity) : UiEvent()

    object GetAllNumbers : UiEvent()
}

sealed class DataEvent : Event {
    data class SuccessGetAllNumbers(val numbersList: List<NumbersEntity>) : DataEvent()
    object OnNumberAdded : DataEvent()
    object OnNumberDeleted : DataEvent()
}

enum class STATUS {
    LOAD,
    CONTENT,
    ERROR
}