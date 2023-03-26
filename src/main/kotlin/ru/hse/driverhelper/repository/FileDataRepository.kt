package ru.hse.driverhelper.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.driverhelper.model.FileData

interface FileDataRepository : JpaRepository<FileData, Long>