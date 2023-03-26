package ru.hse.driverhelper.model

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Contact(
    @Id @GeneratedValue val id: Long? = null,
    var name: String = "",
    var number: String = ""
)