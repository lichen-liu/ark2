package app.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.interfaces.Deserializer;

public abstract class ReadableRepository {
    protected Deserializer deserializer;
    protected Contract contract;

    protected abstract String getAllKeysQuery();

    protected abstract String getKeysByCustomKeysQuery();

    protected abstract String getObjectByKeyQuery();

    public String[] selectObjectKeysByCustomKey(String... customKeys) throws Exception {
        if (customKeys.length == 0) {
            var keys = new String(contract.evaluateTransaction(getAllKeysQuery()));
            return deserializer.toStringArray(keys);
        }

        List<String> usersPostKeys = new ArrayList<String>();
        for (var key : customKeys) {
            var raw = new String(contract.evaluateTransaction(getKeysByCustomKeysQuery(), key));
            var keys = deserializer.toStringArray(raw);
            Collections.addAll(usersPostKeys, keys);
        }

        return usersPostKeys.toArray(String[]::new);
    }

    public String[] selectObjectsByKeys(String... keys) throws ContractException {

        List<String> objects = new ArrayList<String>();
        for (var key : keys) {
            var objectString = new String(contract.evaluateTransaction(getObjectByKeyQuery(), key));
            objects.add(objectString);
        }
        return objects.toArray(String[]::new);
    }

    public String[] selectObjectsByCustomKeys(String... keys) throws Exception {
        return selectObjectsByKeys(selectObjectKeysByCustomKey(keys));
    }
}