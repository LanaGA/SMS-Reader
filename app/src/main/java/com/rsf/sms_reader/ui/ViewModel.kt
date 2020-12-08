package com.rsf.sms_reader.ui

import com.rsf.sms_reader.base.BaseViewModel
import com.rsf.sms_reader.base.Event
import com.rsf.sms_reader.data.local.NumbersRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ViewModel(private val repository: NumbersRepository) : BaseViewModel<ViewState>() {

    override fun initialViewState(): ViewState = ViewState(STATUS.LOAD, listOf())
    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.GetAllNumbers -> {
                repository
                        .getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    processDataEvent(DataEvent.SuccessGetAllNumbers(it))
                                },
                                {
                                    it
                                }
                        )
            }
            is UiEvent.CreateNumber -> {
                repository
                        .create(event.numbersModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    processDataEvent(DataEvent.OnNumberAdded)
                                },
                                {
                                    it
                                }
                        )
            }
            is UiEvent.DeleteNumber -> {
                repository
                        .delete(event.numbersModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    processDataEvent(DataEvent.OnNumberDeleted)
                                },
                                {
                                    it
                                }
                        )
            }
            is DataEvent.SuccessGetAllNumbers -> {
                return previousState.copy(
                        status = STATUS.CONTENT,
                        numbersList = event.numbersList
                )
            }

            is DataEvent.OnNumberDeleted -> {
                return previousState.copy(
                        status = STATUS.LOAD
                )
            }

            is DataEvent.OnNumberAdded -> {
                return previousState.copy(
                        status = STATUS.LOAD
                )
            }
        }
        return null
    }
}