package com.example.calleme.Database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hospital(
    @SerialName("hospital_id") val hospitalId: Long,
    @SerialName("created_at") val createdAt: String, // Using String to represent timestamptz
    @SerialName("name") val name: String,
    @SerialName("location") val location: String,
    @SerialName("ratings") val ratings: Float?,
    @SerialName("have_er_dept") val haveErDept: Boolean,
    @SerialName("image") val image: String?,
    @SerialName("State") val state: String,
    @SerialName("City") val city: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double
)

