package org.ticker.auth.utils;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.ticker.auth.model.User;

import java.util.Collections;

@Service
@AutoConfigureOrder(2)
public class KeycloakUtils {

    public void createUser(Keycloak keycloak, User.TickerUser tickerUser) {
        RealmResource realmResource = keycloak.realm("myrealm");
        UsersResource usersResource = realmResource.users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(tickerUser.getUsername());
        userRepresentation.setEmail(tickerUser.getEmail());
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(tickerUser.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setEnabled(true);
    }
}
