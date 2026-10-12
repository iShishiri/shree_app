package com.shishir.shree.data

import kotlinx.serialization.Serializable

@Serializable
data class ShreeList(
    val id: String,
    val name: String,
    val kind: String,
)

@Serializable
data class Todo(
    val id: String,
    val body: String,
    val done: Boolean,
)

@Serializable
data class NewTodo(
    val body: String,
)
