package ru.hse.driverhelper.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.hse.driverhelper.model.Ride
import ru.hse.driverhelper.repository.RideRepository
import java.util.Date

@Service
class RideService(
    private val rideRepository: RideRepository
) {

    fun create() = rideRepository.save(Ride(startTime = Date()))

    fun finish(id: Long) = rideRepository.findByIdOrNull(id)?.run {
        finishedTime = Date()
        rideRepository.save(this)
    }
}