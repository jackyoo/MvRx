package com.airbnb.mvrx.sample.models

import com.squareup.moshi.Json

data class ToDo(
    @get:Json(name = "id") @Json(name = "id") val id: String,
    @get:Json(name = "title") @Json(name = "title") val title: String,
    @get:Json(name = "completed") @Json(name = "completed") val completed: Boolean
)