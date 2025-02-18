package com.tonic.model;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, VariableInfo> symbols = new HashMap<>();

    public void put(String name, VariableInfo info) {
        symbols.put(name, info);
    }

    public VariableInfo get(String name) {
        if (!symbols.containsKey(name)) {
            throw new RuntimeException("Undefined variable: " + name);
        }
        return symbols.get(name);
    }
}