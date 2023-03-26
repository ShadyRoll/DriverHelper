package ru.hse.driverhelper.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.driverhelper.model.Contact

interface ContactRepository : JpaRepository<Contact, Long>
