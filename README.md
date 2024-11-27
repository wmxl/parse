# Code Parser Demo

A demonstration project for parsing and analyzing source code.

## Overview

This project provides tools and utilities for parsing, analyzing, and manipulating source code. It serves as a demonstration of code parsing techniques using Spring Boot, leveraging both JavaParser and ANTLR4 for different parsing scenarios.

## Features

- Code parsing and tokenization using JavaParser and ANTLR4
- Abstract Syntax Tree (AST) generation
- Code analysis capabilities
    - JavaParser for Java source code analysis
    - ANTLR4 for custom grammar and multi-language support
- Support for multiple programming languages
- Comparison between JavaParser and ANTLR4 approaches

## Prerequisites

- Java 8
- Maven 3.x
- Spring Boot 2.7.18
- JavaParser
- ANTLR4

## Key Components

- **JavaParser**: Used for Java-specific code analysis, providing:
  - AST manipulation
  - Code structure analysis
  - Symbol resolution
  
- **ANTLR4**: Used for:
  - Custom grammar definition
  - Multi-language parsing support
  - Flexible token processing 