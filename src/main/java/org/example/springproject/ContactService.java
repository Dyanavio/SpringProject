package org.example.springproject;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.util.List;

import java.util.List;

@Service
public class ContactService
{
    @Autowired
    private ContactRepository contactRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ContactService()
    {

    }

    @Transactional(readOnly = true)
    public Contact findById(Long id)
    {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact with Id: " + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public List<Contact> findAll()
    {
        return contactRepository.findAll();
    }

    @Transactional
    public void saveContact(String firstName, String lastName, String number)
    {
        var newContact = new Contact();
        newContact.setFirstName(firstName);
        newContact.setLastName(lastName);
        newContact.setNumber(number);
        contactRepository.save(newContact);
    }

    @Transactional
    public void updateContact(Long id, String firstName, String lastName, String number)
    {
        var contact = findById(id);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setNumber(number);
        contactRepository.save(contact);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)

    public void deleteContact(Long id)
    {
        if (!contactRepository.existsById(id))
        {
            throw new EntityNotFoundException("Contact with Id " + id + " was not found");
        }
        contactRepository.deleteById(id);
    }
}
