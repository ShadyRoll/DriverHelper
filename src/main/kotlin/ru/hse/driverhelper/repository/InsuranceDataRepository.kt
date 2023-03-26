package ru.hse.driverhelper.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.driverhelper.model.InsuranceData

interface InsuranceDataRepository : JpaRepository<InsuranceData, Long>