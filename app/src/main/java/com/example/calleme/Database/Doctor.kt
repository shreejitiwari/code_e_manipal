package com.example.calleme.Database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Doctor(
    @SerialName("doc_id") val docId: Long,
    @SerialName("hospital_id") val hospitalId: Long,
    @SerialName("name") val name: String,
    @SerialName("specialization") val specialization: String? = "Unknown",
    @SerialName("rating") val rating: Float?,
    @SerialName("Experience") val experience: Int? = 0,
    @SerialName("is_available_now") val isAvailable: Boolean,
    @SerialName("avl_time_slots") val availableTimeSlots: List<String>? = emptyList(),
    @SerialName("avl_days") val availableDays: List<String>? = emptyList(),
    @SerialName("profile_photo") val profilePhoto: String? = null
)