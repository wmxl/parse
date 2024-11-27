package com.parse.demo.antlr;

import com.parse.demo.Endpoint;
import com.parse.demo.Java8Lexer;
import com.parse.demo.Java8Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Antlr {
    private static final String REPO_URL = "https://github.com/wmxl/parse.git";
    private static final String LOCAL_PATH = "/Users/admin/IdeaProjects/download";

    public static void main(String[] args) {
        List<Endpoint> endpoints = new ArrayList<>();
        Map<String, String> constantsMap = new HashMap<>();

        try {
            // Clone the repository
            cloneRepository();
            
            // Find all Java files
            File repoDir = new File(LOCAL_PATH);
            List<File> javaFiles = findJavaFiles(repoDir);
            
            // First parse constants file
            File configFile = findConfigFile(javaFiles);
            if (configFile != null) {
//                System.out.println("Parsing constants file...");
                parseConstantsFile(configFile.getPath(), constantsMap);
                
                // Print parsed constants for verification
//                System.out.println("\nParsed constants:");
                constantsMap.forEach((key, value) -> 
                    System.out.println(key + " = " + value));
            }

            // Then parse controller files
//            System.out.println("\nParsing controller files...");
            for (File file : javaFiles) {
                if (isControllerFile(file)) {
                    parseFile(file.getPath(), new EndpointListener(endpoints, constantsMap));
                }
            }

            // Print results
            System.out.println("\nFound " + endpoints.size() + " endpoints:");
            for (Endpoint endpoint : endpoints) {
                System.out.println("Method: " + endpoint.method);
                System.out.println("Type: " + endpoint.type);
                System.out.println("Path: " + endpoint.path);
                System.out.println("-------------------");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
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
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        javaFiles.addAll(findJavaFiles(file));
                    } else if (file.getName().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                }
            }
        }
        return javaFiles;
    }

    private static File findConfigFile(List<File> javaFiles) {
        return javaFiles.stream()
                .filter(file -> file.getName().endsWith("Config.java"))
                .findFirst()
                .orElse(null);
    }

    private static boolean isControllerFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("@Controller") || line.contains("@RestController")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void parseFile(String filePath, ParseTreeListener listener) throws IOException {
//        System.out.println("Processing file: " + filePath);
        
        CharStream input = CharStreams.fromFileName(filePath);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                  int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("Warning: Parse error at line " + line + ":" + charPositionInLine + " " + msg);
            }
        });

        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
    }

    private static void parseConstantsFile(String filePath, Map<String, String> constantsMap) throws IOException {
        Pattern pattern = Pattern.compile("String\\s+(\\w+)\\s*=\\s*\"([^\"]+)\"");
        String interfaceName = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("interface")) {
                    Pattern interfacePattern = Pattern.compile("interface\\s+(\\w+)");
                    Matcher m = interfacePattern.matcher(line);
                    if (m.find()) {
                        interfaceName = m.group(1);
//                        System.out.println("Found interface: " + interfaceName);
                    }
                }

                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    String constantName = interfaceName + "." + m.group(1);
                    String constantValue = m.group(2);
//                    System.out.println("Adding constant: " + constantName + " = " + constantValue);
                    constantsMap.put(constantName, constantValue);
                }
            }
            System.out.println("Constants map after parsing: " + constantsMap);
        }
    }
} 