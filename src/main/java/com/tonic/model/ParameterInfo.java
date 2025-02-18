package com.tonic.model;

import com.tonic.model.antlr.TLangParser;

public class ParameterInfo {
    public final Type type;
    public final String name;
    public final TLangParser.ExpressionContext defaultExpr; // null if none

    public ParameterInfo(Type type, String name, TLangParser.ExpressionContext defaultExpr) {
        this.type = type;
        this.name = name;
        this.defaultExpr = defaultExpr;
    }
}
