package com.uit.taskmanagement

data class CardInfo(
    var title: String ?= null,
    var priority: String ?= null,
    var date: String ?= null,
    var icon: String ?= null,
    var done: String ?= null,
    var idTask: String ?= null,
    var idUser: String ?= null
)
