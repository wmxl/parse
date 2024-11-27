package com.parse.demo.antlr;


import com.parse.demo.Java8BaseListener;
import com.parse.demo.Java8Parser;
import java.util.Map;

public class ConstantsListener extends Java8BaseListener {
    private Map<String, String> constantsMap;
    private String currentInterfaceName;

    public ConstantsListener(Map<String, String> constantsMap) {
        this.constantsMap = constantsMap;
    }

    @Override
    public void enterInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
        if (ctx != null && ctx.Identifier() != null) {
            currentInterfaceName = ctx.Identifier().getText();
        }
    }


} 