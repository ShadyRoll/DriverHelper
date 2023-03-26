package ru.hse.driverhelper.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.hse.driverhelper.model.Contact
import ru.hse.driverhelper.model.Driver
import ru.hse.driverhelper.model.InsuranceData
import ru.hse.driverhelper.model.MedicineData
import ru.hse.driverhelper.model.Ride
import ru.hse.driverhelper.model.VehicleMaintenanceData
import ru.hse.driverhelper.repository.DriverRepository
import ru.hse.driverhelper.repository.FileDataRepository
import ru.hse.driverhelper.repository.InsuranceDataRepository
import ru.hse.driverhelper.repository.MedicineDataRepository
import ru.hse.driverhelper.repository.VehicleMaintenanceDataRepository
import ru.hse.driverhelper.repository.update
import kotlin.random.Random

@Service
class DriverService(
    private val driverRepository: DriverRepository,
    private val medicineDataRepository: MedicineDataRepository,
    private val insuranceDataRepository: InsuranceDataRepository,
    private val vehicleMaintenanceDataRepository: VehicleMaintenanceDataRepository,
    private val fileDataRepository: FileDataRepository,
) : UserDetailsService {
    val random = Random.Default

    fun get(id: Long) = driverRepository.findByIdOrNull(id)

    fun getByLogin(login: String) = driverRepository.findByLogin(login)

    fun save(driver: Driver) = driverRepository.save(driver)

    fun update(login: String, driver: Driver): Driver {
        val id = getByLogin(login)!!.id!!
        return driverRepository.update(id) {
            it.bloodType = driver.bloodType ?: it.bloodType
            it.rhFactor = driver.rhFactor ?: it.rhFactor
            it.maxDriveDurationSeconds = driver.maxDriveDurationSeconds ?: it.maxDriveDurationSeconds
            it.phoneNumber = driver.phoneNumber ?: it.phoneNumber
            it.chronicDisease = driver.chronicDisease ?: it.chronicDisease
            it.myMedicines = driver.myMedicines ?: it.myMedicines
            it.name = driver.name ?: it.name
            it.surname = driver.surname ?: it.surname
            it.middleName = driver.middleName ?: it.middleName
        }
    }

    fun updateMedicineData(login: String, medicineData: MedicineData): MedicineData {
        val driver = driverRepository.findByLogin(login)!!
        fileDataRepository.saveAndFlush(medicineData.file!!)
        val newData =
            medicineDataRepository.saveAndFlush(medicineData.copy(id = driver.medicineData?.id ?: random.nextLong()))
        return driverRepository.update(driver.id!!) { it.medicineData = newData }.medicineData!!
    }

    fun updateInsuranceData(login: String, insuranceData: InsuranceData): InsuranceData {
        val driver = driverRepository.findByLogin(login)!!
        fileDataRepository.saveAndFlush(insuranceData.file!!)
        val newData =
            insuranceDataRepository.saveAndFlush(insuranceData.copy(id = driver.insuranceData?.id ?: random.nextLong()))
        return driverRepository.update(driver.id!!) { it.insuranceData = newData }.insuranceData!!
    }

    fun updateVehicleMaintenanceData(
        login: String,
        vehicleMaintenanceData: VehicleMaintenanceData,
    ): VehicleMaintenanceData {
        val driver = driverRepository.findByLogin(login)!!
        fileDataRepository.saveAndFlush(vehicleMaintenanceData.file!!)
        val newData =
            vehicleMaintenanceDataRepository.saveAndFlush(vehicleMaintenanceData.copy(id = driver.vehicleMaintenanceData?.id
                ?: random.nextLong()))
        return driverRepository.update(driver.id!!) { it.vehicleMaintenanceData = newData }.vehicleMaintenanceData!!
    }

    fun setRide(login: String, ride: Ride) =
        driverRepository.update(getByLogin(login)!!.id!!) { it.currentRide = ride }

    fun existsByLogin(name: String) = driverRepository.existsByLogin(name)

    fun addContact(login: String, contact: Contact) =
        driverRepository.update(driverRepository.findByLogin(login)!!.id!!) { it.contacts = it.contacts + contact }

    override fun loadUserByUsername(username: String?): UserDetails {
        return driverRepository.findByLogin(username ?: "") ?: throw UsernameNotFoundException("User not found")
    }
}
