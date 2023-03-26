package ru.hse.driverhelper.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.driverhelper.model.Contact
import ru.hse.driverhelper.model.Driver
import ru.hse.driverhelper.repository.ContactRepository
import ru.hse.driverhelper.repository.DriverRepository
import ru.hse.driverhelper.repository.update

@Service
@Transactional
class ContactService(
    private val contactRepository: ContactRepository,
    private val driverRepository: DriverRepository,
) {
    fun save(contact: Contact) = contactRepository.saveAndFlush(contact)

    fun put(contactId: Long, contact: Contact) =
        contactRepository.update(contactId) {
            it.name = contact.name
            it.number = contact.number
        }

    fun delete(contactId: Long, driver: Driver) {
        driverRepository.update(driver.id!!) {
            it.contacts = it.contacts.filterNot { contact -> contact.id == contactId }
        }
        contactRepository.deleteById(contactId)
    }
}