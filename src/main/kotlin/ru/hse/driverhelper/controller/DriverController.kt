package ru.hse.driverhelper.controller

import org.springframework.http.MediaType
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.hse.driverhelper.auth.JwtAuthentication
import ru.hse.driverhelper.model.Driver
import ru.hse.driverhelper.model.FileData
import ru.hse.driverhelper.model.InsuranceData
import ru.hse.driverhelper.model.MedicineData
import ru.hse.driverhelper.model.VehicleMaintenanceData
import ru.hse.driverhelper.service.DriverService
import java.util.Date

@RestController("driver")
@RequestMapping("/driver")
@Transactional
class DriverController(
    private val driverService: DriverService,
) {

    @GetMapping
    fun get(principal: JwtAuthentication) =
        driverService.getByLogin(principal.username!!)

    @PutMapping
    fun update(@RequestBody driver: Driver, principal: JwtAuthentication) =
        driverService.update(principal.username!!, driver)

    @PutMapping("/medicine", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateMedicineData(
        @RequestPart file: MultipartFile,
        @RequestParam fileName: String,
        @RequestParam expirationDate: Date? = null,
        principal: JwtAuthentication,
    ) =
        driverService.updateMedicineData(
            principal.username!!,
            MedicineData(
                file = FileData(name = fileName, content = file.bytes),
                expirationDate = expirationDate
            )
        )

    @GetMapping("/medicine")
    fun getMedicineData(principal: JwtAuthentication) =
        driverService.getByLogin(principal.username!!)!!.medicineData

    @GetMapping("/insurance")
    fun getInsuranceData(principal: JwtAuthentication) = driverService.getByLogin(principal.username!!)!!.insuranceData

    @PutMapping("/insurance")
    fun updateInsuranceData(
        @RequestPart file: MultipartFile,
        @RequestParam fileName: String,
        @RequestParam expirationDate: Date? = null,
        principal: JwtAuthentication,
    ) =
        driverService.updateInsuranceData(
            principal.username!!,
            InsuranceData(
                file = FileData(name = fileName, content = file.bytes),
                expirationDate = expirationDate
            )
        )

    @GetMapping("/vehicle_maintenance")
    fun getVehicleMaintenanceData(principal: JwtAuthentication) =
        driverService.getByLogin(principal.username!!)!!.vehicleMaintenanceData

    @PutMapping("/vehicle_maintenance")
    fun updateVehicleMaintenanceData(
        @RequestPart file: MultipartFile,
        @RequestParam fileName: String,
        @RequestParam expirationDate: Date? = null,
        principal: JwtAuthentication,
    ) =
        driverService.updateVehicleMaintenanceData(
            principal.username!!,
            VehicleMaintenanceData(
                file = FileData(name = fileName, content = file.bytes),
                expirationDate = expirationDate
            )
        )
}
