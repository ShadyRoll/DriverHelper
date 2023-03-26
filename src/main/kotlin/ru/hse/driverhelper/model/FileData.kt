package ru.hse.driverhelper.model

import java.sql.Blob
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob

@Entity
data class FileData(
    @Id @GeneratedValue val id: Long? = null,
    val name: String = "",
    @Lob val content: ByteArray? = null
)
