package ru.hse.driverhelper.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import ru.hse.driverhelper.model.MedicineData

interface MedicineDataRepository : JpaRepository<MedicineData, Long>

fun <T> JpaRepository<T, Long>.update(id: Long, modify: (T) -> Unit) = findByIdOrNull(id)!!.let {
    modify(it)
    save(it)
}
