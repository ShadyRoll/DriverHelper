package ru.hse.driverhelper.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.driverhelper.model.Driver

interface DriverRepository : JpaRepository<Driver, Long> {
    fun findByLogin(login: String): Driver?
    fun existsByLogin(login: String): Boolean
}

