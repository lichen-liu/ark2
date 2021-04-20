package app.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.utils.Deserializer;

public abstract class ReadableRepository<T> {
    protected Deserializer deserializer;
    protected Contract contract;

    protected abstract String getAllKeysQuery();

    protected abstract String getKeysByCustomKeysQuery();

    protected abstract String getObjectByKeyQuery();

    private final ObjectMapper om = new ObjectMapper();
    private final Class<T> dataType;

    public ReadableRepository(final Class<T> dataType) {
        this.dataType = dataType;
    }

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

    public List<T> selectObjectsByKeys(final String... keys)
            throws ContractException, JsonParseException, JsonMappingException, IOException {

        final var objects = new ArrayList<T>();
        for (final var key : keys) {
            final String raw = new String(contract.evaluateTransaction(getObjectByKeyQuery(), key));
            final T data = om.readValue(raw, dataType);
            objects.add(data);
        }

        return objects;
    }

    public List<T> selectObjectsByCustomKeys(final String... keys) throws Exception {
        return selectObjectsByKeys(selectObjectKeysByCustomKey(keys));
    }
}