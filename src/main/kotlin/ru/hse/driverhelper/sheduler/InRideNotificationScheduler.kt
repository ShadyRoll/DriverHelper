package ru.hse.driverhelper.sheduler

import org.springframework.stereotype.Component
import ru.hse.driverhelper.service.NotificationService

@Component
class InRideNotificationScheduler(
    private val notificationService: NotificationService
)