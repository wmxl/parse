package com.parse.demo.javaparse;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.parse.demo.Endpoint;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaParse {
    private static Map<String, String> constantsMap = new HashMap<>();
    private static final String REPO_URL = "https://github.com/wmxl/parse.git";
    private static final String LOCAL_PATH = "/Users/admin/IdeaProjects/download";

    public static void main(String[] args) {
        List<Endpoint> endpoints = new ArrayList<>();
        try {
            // Clone the repository
            cloneRepository();
            
            // Find and parse all Java files in the repository
            File repoDir = new File(LOCAL_PATH);
            List<File> javaFiles = findJavaFiles(repoDir);
            
            // Parse constants first (assuming MomentUrlConfig exists in the repo)
            parseConstants(LOCAL_PATH + "/src/main/java/com/parse/demo/web/config/MomentUrlConfig.java");

            // Parse each controller file
            for (File file : javaFiles) {
                parseControllerFile(file, endpoints);
            }

            System.out.println("Found " + endpoints.size() + " endpoints:");
            endpoints.forEach(endpoint -> {
                System.out.println("Method: " + endpoint.method);
                System.out.println("Type: " + endpoint.type);
                System.out.println("Path: " + endpoint.path);
                System.out.println("-------------------");
            });

        } catch (GitAPIException e) {
            System.err.println("Git operation failed: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + e.getMessage());
        }
    }

    private static void cloneRepository() throws GitAPIException {
        File directory = new File(LOCAL_PATH);
        if (directory.exists()) {
            deleteDirectory(directory);
        }
        
        Git.cloneRepository()
           .setURI(REPO_URL)
           .setDirectory(directory)
           .call();
    }

    // Recursively delete a directory
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private static List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    private static void parseControllerFile(File file, List<Endpoint> endpoints) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                boolean isController = classDecl.getAnnotations().stream()
                        .anyMatch(a -> a.getNameAsString().equals("Controller") || 
                                     a.getNameAsString().equals("RestController"));
                
                if (isController) {
                    String basePath = classDecl.getAnnotationByName("RequestMapping")
                        .map(a -> resolveConstantExpression(a.getChildNodes().get(1).toString()))
                        .orElse("");
                    
                    classDecl.getMethods().forEach(method -> {
                        method.getAnnotations().forEach(annotation -> {
                            String annotationName = annotation.getNameAsString();
                            if (annotationName.contains("Mapping")) {
                                endpoints.add(new Endpoint(
                                    method.getNameAsString(),
                                    annotationName,
                                    basePath + resolveConstantExpression(getPathFromAnnotation(annotation))
                                ));
                            }
                        });
                    });
                }
            });
        } catch (FileNotFoundException e) {
            System.err.println("Error parsing file " + file.getName() + ": " + e.getMessage());
        }
    }

    private static void parseConstants(String configFilePath) throws FileNotFoundException {
        CompilationUnit configCU = StaticJavaParser.parse(new File(configFilePath));
        configCU.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            String className = classDecl.getNameAsString();
            classDecl.getFields().forEach(field -> {
                field.getVariables().forEach(var -> {
                    String constantName = className + "." + var.getNameAsString();
                    var.getInitializer().ifPresent(init -> 
                        constantsMap.put(constantName, init.toString().replace("\"", ""))
                    );
                });
            });
        });
    }

    private static String resolveConstantExpression(String expression) {
        // Remove any surrounding quotes from the entire expression
        expression = expression.replaceAll("^\"(.*)\"$", "$1");
        
        // Handle expressions like 'MomentUrlConfig.APP_URL_PREFIX + "/moment"'
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                // Remove quotes from string literals
                part = part.replaceAll("^\"(.*)\"$", "$1");
                if (constantsMap.containsKey(part)) {
                    // Constant reference
                    result.append(constantsMap.get(part));
                } else {
                    // String literal or unresolved reference
                    result.append(part);
                }
            }
            return result.toString();
        }
        
        // Handle single constant reference
        return constantsMap.getOrDefault(expression, expression);
    }

    private static String getPathFromAnnotation(AnnotationExpr annotation) {
        return annotation.getChildNodes().size() > 1 ? 
               annotation.getChildNodes().get(1).toString() : "";
    }
}
