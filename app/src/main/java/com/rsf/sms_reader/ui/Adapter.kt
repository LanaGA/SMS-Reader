package com.rsf.sms_reader.ui

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.rsf.sms_reader.R
import com.rsf.sms_reader.data.local.base.NumbersEntity
import kotlinx.android.synthetic.main.item_number.*

fun adapterDelegate(onClick: (NumbersEntity) -> Unit): AdapterDelegate<List<NumbersEntity>> =
        adapterDelegateLayoutContainer<NumbersEntity, NumbersEntity>(
                R.layout.item_number
        ) {
            bind {
                textName.text = item.number
            deleteButton.setOnClickListener {
                onClick(item)
            }}
        }