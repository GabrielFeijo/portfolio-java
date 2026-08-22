package br.com.gabrielfeijo.portfolio.domain.event;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;

public record ContactCreatedEvent(Contact contact) {
}
