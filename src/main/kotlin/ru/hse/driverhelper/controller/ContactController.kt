package ru.hse.driverhelper.controller

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.Forbidden
import ru.hse.driverhelper.auth.JwtAuthentication
import ru.hse.driverhelper.model.Contact
import ru.hse.driverhelper.service.ContactService
import ru.hse.driverhelper.service.DriverService

@RestController("contact")
@RequestMapping("/contact")
@Transactional
class ContactController(
    private val contactService: ContactService,
    private val driverService: DriverService,
) {
    @GetMapping
    fun getContacts(principal: JwtAuthentication): List<Contact> =
        driverService.getByLogin(principal.username!!)?.contacts ?: listOf()

    @PutMapping("{contactId}")
    fun putContact(@PathVariable contactId: Long, @RequestBody contact: Contact, principal: JwtAuthentication) =
        contactService.put(contactId, contact)

    @PostMapping
    fun addNewContact(@RequestBody contact: Contact, principal: JwtAuthentication) =
        contactService.save(contact).apply { driverService.addContact(principal.username!!, this) }

    @DeleteMapping("{contactId}")
    fun deleteContact(@PathVariable contactId: Long, principal: JwtAuthentication) {
        val driver = driverService.getByLogin(principal.username!!)!!
        if (contactId !in driver.contacts.map { it.id })
            throw IllegalArgumentException("Your account don't own contact with this id")
        contactService.delete(contactId, driver)
    }
}