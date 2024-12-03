package com.project.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.module.Contact;
import com.project.repository.ContactJpaRepository;

@Service
public class ContactJpaService {
    @Autowired
    private ContactJpaRepository contactRepository;

    public Contact getContactById(long id){
        return contactRepository.findById(id).get();
    }

    public List<Contact> getAllContacts(){
        return contactRepository.findAll();
    }

    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public List<Contact> getAllPendingContacts(String status){
        return contactRepository.findByStatus(status);
    }
}

