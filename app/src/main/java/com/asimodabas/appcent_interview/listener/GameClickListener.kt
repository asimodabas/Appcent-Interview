package com.asimodabas.appcent_interview.listener

import com.asimodabas.appcent_interview.model.Result

interface GameClickListener {
    fun GameClick(vie: Result)
    fun GameFilter(nameLength: Int)
}