package com.parse.demo.antlr;

import com.parse.demo.Java8BaseListener;
import com.parse.demo.Java8Parser;
import com.parse.demo.Endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndpointListener extends Java8BaseListener {
    private List<Endpoint> endpoints;
    private Map<String, String> constantsMap;
    private String basePath;

    public EndpointListener(List<Endpoint> endpoints, Map<String, String> constantsMap) {
        this.endpoints = endpoints;
        this.constantsMap = constantsMap;
        this.basePath = constantsMap.get("MomentUrlConfig.APP_URL_PREFIX");
//        System.out.println("Initial basePath from constants: " + this.basePath);
    }

    @Override
    public void enterClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        // Get the full class text including annotations
        String classText = ctx.getParent().getText();  // Get parent to include annotations
//        System.out.println("\nDEBUG: Full class text:");
//        System.out.println(classText);
        
        findMappings(classText);
    }

    private void findMappings(String text) {
        // First find class-level RequestMapping with constant reference
        Pattern basePathPattern = Pattern.compile("@RequestMapping\\((MomentUrlConfig\\.\\w+\\s*\\+\\s*\"[^\"]*\"|[^)]+)\\)");
        Matcher basePathMatcher = basePathPattern.matcher(text);
        if (basePathMatcher.find()) {
            String basePathExpr = basePathMatcher.group(1);
//            System.out.println("Found class-level RequestMapping: " + basePathExpr);
            
            // If it contains MomentUrlConfig, handle it specially
            if (basePathExpr.contains("MomentUrlConfig")) {
                String[] parts = basePathExpr.split("\\+");
                StringBuilder resolvedPath = new StringBuilder();
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("MomentUrlConfig.")) {
                        String value = constantsMap.get(part);
//                        System.out.println("Resolved " + part + " to: " + value);
                        resolvedPath.append(value);
                    } else {
                        // Remove quotes
                        part = part.replaceAll("\"", "");
                        resolvedPath.append(part);
                    }
                }
                basePath = resolvedPath.toString();
            } else {
                basePath = resolveConstantExpression(basePathExpr);
            }
//            System.out.println("Resolved basePath: " + basePath);
        }

        // Then find method mappings
        Map<String, Pattern> mappingPatterns = new HashMap<>();
        String methodPattern = "public(?:Object|String|void|\\w+)(\\w+)\\(";
        mappingPatterns.put("GetMapping", Pattern.compile("@GetMapping\\([\"']([^\"']*)[\"']\\)" + methodPattern));
        mappingPatterns.put("PostMapping", Pattern.compile("@PostMapping\\([\"']([^\"']*)[\"']\\)" + methodPattern));
        mappingPatterns.put("RequestMapping", Pattern.compile("@RequestMapping\\([\"']([^\"']*)[\"']\\)" + methodPattern));

        for (Map.Entry<String, Pattern> entry : mappingPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(text);
            while (matcher.find()) {
                String path = matcher.group(1);
                String methodName = matcher.group(2);
//                System.out.println("Found mapping - Type: " + entry.getKey() + ", Path: " + path + ", Method: " + methodName);
                
                String fullPath = basePath + resolveConstantExpression(path);
//                System.out.println("Constructed fullPath: " + fullPath);
                
                endpoints.add(new Endpoint(
                    methodName,
                    entry.getKey(),
                    fullPath
                ));
            }
        }
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        String methodText = ctx.getText();
//        System.out.println("\nDEBUG: Method text:");
//        System.out.println(methodText);
    }

    private String resolveConstantExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }
        
//        System.out.println("Resolving expression: " + expression);
        expression = expression.trim().replaceAll("\"", "");
        
        if (expression.startsWith("MomentUrlConfig.")) {
            String value = constantsMap.get(expression);
//            System.out.println("Resolved constant " + expression + " to: " + value);
            return value != null ? value : expression;
        }
        
        return expression;
    }
} 