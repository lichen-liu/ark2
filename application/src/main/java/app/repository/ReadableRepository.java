package app.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.utils.Deserializer;

public abstract class ReadableRepository {
    protected Deserializer deserializer;
    protected Contract contract;

    protected abstract String getAllKeysQuery();

    protected abstract String getKeysByCustomKeysQuery();

    protected abstract String getObjectByKeyQuery();

    public String[] selectObjectKeysByCustomKey(final String... customKeys) throws Exception {
        if (customKeys.length == 0) {
            final var keys = new String(contract.evaluateTransaction(getAllKeysQuery()));
            return deserializer.toStringArray(keys);
        }

        final List<String> usersPostKeys = new ArrayList<String>();
        for (final var key : customKeys) {
            final var raw = new String(contract.evaluateTransaction(getKeysByCustomKeysQuery(), key));
            final var keys = deserializer.toStringArray(raw);
            Collections.addAll(usersPostKeys, keys);
        }

        return usersPostKeys.toArray(String[]::new);
    }

    public String[] selectObjectsByKeys(final String... keys) throws ContractException {

        final List<String> objects = new ArrayList<String>();
        for (final var key : keys) {
            final var objectString = new String(contract.evaluateTransaction(getObjectByKeyQuery(), key));
            objects.add(objectString);
        }
        return objects.toArray(String[]::new);
    }

    public String[] selectObjectsByCustomKeys(final String... keys) throws Exception {
        return selectObjectsByKeys(selectObjectKeysByCustomKey(keys));
    }
}