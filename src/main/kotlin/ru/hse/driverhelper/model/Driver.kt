package ru.hse.driverhelper.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@JsonIgnoreProperties(
    value = ["passwordHash", "password", "username", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"]
)
@Entity
data class Driver(
    @Id @GeneratedValue val id: Long? = null,
    val login: String = "",
    var name: String? = null,
    var surname: String? = null,
    var middleName:  String? = null,
    val passwordHash: String = "",
    @OneToOne
    var medicineData: MedicineData? = null,
    @OneToOne
    var insuranceData: InsuranceData? = null,
    @OneToOne
    var vehicleMaintenanceData: VehicleMaintenanceData? = null,
    @OneToOne
    var currentRide: Ride? = null,
    @OneToMany
    var contacts: List<Contact> = listOf(),
    var bloodType: BloodType? = null,
    var rhFactor: RhFactor? = null,
    var maxDriveDurationSeconds: Long? = null,
    var phoneNumber: String? = null,
    var chronicDisease: String? = null,
    var myMedicines: String? = null,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("user"))

    override fun getPassword(): String = passwordHash

    override fun getUsername(): String = login

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

enum class BloodType(@JsonValue val jsonName: String) {
    O_1("O (I)"), A_2("A (II)"), B_3("B (III)"), AB_4("C (IV)")
}

enum class RhFactor(@JsonValue val jsonName: String){
    POSITIVE("Rh+"), NEGATIVE("Rh-")
}

@Entity
data class InsuranceData(
    @Id @GeneratedValue val id: Long? = null,
    @OneToOne
    val file: FileData? = null,
    val expirationDate: Date? = null,
)

@Entity
data class MedicineData(
    @Id @GeneratedValue val id: Long? = null,
    @OneToOne
    val file: FileData? = null,
    val expirationDate: Date? = null,
)

@Entity
data class VehicleMaintenanceData(
    @Id @GeneratedValue val id: Long? = null,
    @OneToOne
    val file: FileData? = null,
    val expirationDate: Date? = null,
)