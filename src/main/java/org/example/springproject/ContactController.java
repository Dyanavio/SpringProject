package org.example.springproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController
{
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    public ResponseEntity<Object> getOneOrAll(@RequestParam(name = "id", required = false) Long id)
    {
        if(id != null)
        {
            Contact contact = contactRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact with Id " + id + " was not found"));
            return ResponseEntity.ok(contact);
        }
        return ResponseEntity.ok(contactRepository.findAll());
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteContact(@RequestParam(name = "id") Long id)
    {
        try
        {
            contactService.deleteContact(id);
            return ResponseEntity.ok("Contact was successfully deleted");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.ok("Contact with Id " + id + " was not found.");
        }
        catch (Exception e)
        {
            System.out.println("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error on deleting a contact: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> saveContact(@RequestParam(required = false, name = "_method") String _method,
                                              @RequestParam(required = false, name = "id") Long id, @RequestParam(name = "firstName") String firstName,
                                              @RequestParam(required = false, name = "lastName") String lastName,
                                              @RequestParam(required = false, name = "number") String number)
    {
        if ("PUT".equalsIgnoreCase(_method) && id != null)
        {
            return contactRepository.findById(id).map(contact -> {
                contact.setFirstName(firstName);
                contact.setLastName(lastName);
                contact.setNumber(number);
                contactRepository.save(contact);
                return ResponseEntity.ok("Contact: " + contact.getFirstName() + contact.getLastName());
            }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact was not found"));
        }
        else
        {
            var newContact = new Contact();
            newContact.setFirstName(firstName);
            newContact.setLastName(lastName);
            newContact.setNumber(number);
            contactRepository.save(newContact);
            return ResponseEntity.status(HttpStatus.CREATED).body("Contact was created: " + newContact.getFirstName() + newContact.getLastName());
        }
    }

}

