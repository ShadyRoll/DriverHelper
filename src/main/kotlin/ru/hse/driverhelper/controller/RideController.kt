package ru.hse.driverhelper.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.hse.driverhelper.auth.JwtAuthentication
import ru.hse.driverhelper.service.DriverService
import ru.hse.driverhelper.service.RideService
import java.security.Principal

@RestController("ride")
@RequestMapping("/ride")
class RideController(
    private val rideService: RideService,
    private val driverService: DriverService
) {
    @GetMapping("current")
    fun getCurrentRide(principal: JwtAuthentication) =
        driverService.get(driverService.getByLogin(principal.username!!)!!.id!!)?.currentRide

    @PostMapping("start")
    fun startRide(principal: JwtAuthentication): String =
        rideService.create().apply { driverService.setRide(principal.username!!, this) }.id.toString()

    @PostMapping("/finish")
    fun finishRide(principal: JwtAuthentication) =
        rideService.finish(driverService.get(driverService.getByLogin(principal.username!!)!!.id!!)!!.currentRide!!.id!!)
}