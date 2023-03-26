package ru.hse.driverhelper.model

import java.util.Date
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Ride(
    @Id @GeneratedValue val id: Long? = null,
    val startTime: Date = Date(),
    val needRestNotificationSent: Boolean = false,
    var finishedTime: Date? = null
)
