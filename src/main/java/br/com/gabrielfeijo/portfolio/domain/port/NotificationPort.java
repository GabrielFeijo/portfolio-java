package br.com.gabrielfeijo.portfolio.domain.port;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;

public interface NotificationPort {

    void sendContactNotification(Contact contact);
}
