package app.user.service;

import org.hyperledger.fabric.gateway.Contract;

import app.user.Anonymous;
import app.user.RepositoryProvider;

public class AnonymousAppUser extends RepositoryProvider implements Anonymous {
    public AnonymousAppUser(final Contract contract) {
        super(contract);
    }
}
