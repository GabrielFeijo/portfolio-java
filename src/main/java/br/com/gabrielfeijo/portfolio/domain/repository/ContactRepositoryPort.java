package br.com.gabrielfeijo.portfolio.domain.repository;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;

import java.util.Optional;

public interface ContactRepositoryPort {
    Contact save(Contact contact);
    Optional<Contact> findById(String id);
}
