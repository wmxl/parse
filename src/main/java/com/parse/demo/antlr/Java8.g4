grammar Java8;

@header {
package com.parse.demo;
}

// Parser Rules
compilationUnit
    : packageDeclaration? importDeclaration* typeDeclaration* EOF
    ;

packageDeclaration
    : annotation* 'package' qualifiedName ';'
    ;

importDeclaration
    : 'import' 'static'? qualifiedName ('.' '*')? ';'
    ;

typeDeclaration
    : classOrInterfaceModifier* classDeclaration
    | ';'
    ;

classDeclaration
    : 'class' Identifier typeParameters? 
      ('extends' typeType)? 
      ('implements' typeList)?
      classBody
    ;

classBody
    : '{' classBodyDeclaration* '}'
    ;

classBodyDeclaration
    : ';'
    | 'static'? block
    | modifier* memberDeclaration
    ;

memberDeclaration
    : fieldDeclaration
    | methodDeclaration
    // Add other members as needed
    ;

fieldDeclaration
    : typeType variableDeclarators ';'
    ;

methodDeclaration
    : typeTypeOrVoid Identifier formalParameters ('[' ']')*
      ('throws' qualifiedNameList)?
      (methodBody | ';')
    ;

methodBody
    : block
    ;

typeParameters
    : '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    : Identifier ('extends' typeBound)?
    ;

typeBound
    : typeType ('&' typeType)*
    ;

modifier
    : classOrInterfaceModifier
    | ( 'native'
      | 'synchronized'
      | 'transient'
      | 'volatile'
      )
    ;

classOrInterfaceModifier
    : annotation
    | ( 'public'
      | 'protected'
      | 'private'
      | 'static'
      | 'abstract'
      | 'final'
      | 'strictfp'
      )
    ;

variableDeclarators
    : variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : variableDeclaratorId ('=' variableInitializer)?
    ;

variableDeclaratorId
    : Identifier ('[' ']')*
    ;

variableInitializer
    : arrayInitializer
    | expression
    ;

arrayInitializer
    : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
    ;

typeType
    : classOrInterfaceType ('[' ']')*
    | primitiveType ('[' ']')*
    ;

classOrInterfaceType
    : Identifier typeArguments? ('.' Identifier typeArguments?)*
    ;

typeArguments
    : '<' typeArgument (',' typeArgument)* '>'
    ;

typeArgument
    : typeType
    | '?' (('extends' | 'super') typeType)?
    ;

qualifiedNameList
    : qualifiedName (',' qualifiedName)*
    ;

formalParameters
    : '(' formalParameterList? ')'
    ;

formalParameterList
    : formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    | lastFormalParameter
    ;

formalParameter
    : variableModifier* typeType variableDeclaratorId
    ;

lastFormalParameter
    : variableModifier* typeType '...' variableDeclaratorId
    ;

qualifiedName
    : Identifier ('.' Identifier)*
    ;

literal
    : IntegerLiteral
    | FloatingPointLiteral
    | CharacterLiteral
    | StringLiteral
    | BooleanLiteral
    | NullLiteral
    ;

// Annotations
annotation
    : '@' qualifiedName ('(' ( elementValuePairs | elementValue )? ')')?
    ;

elementValuePairs
    : elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    : Identifier '=' elementValue
    ;

elementValue
    : expression
    | annotation
    | elementValueArrayInitializer
    ;

elementValueArrayInitializer
    : '{' (elementValue (',' elementValue)* (',')?)? '}'
    ;

annotationTypeDeclaration
    : '@' 'interface' Identifier annotationTypeBody
    ;

// Expression
expression
    : primary
    | expression '.' Identifier
    | expression '.' 'this'
    | expression '.' 'new' nonWildcardTypeArguments? innerCreator
    | expression '.' 'super' superSuffix
    | expression '.' explicitGenericInvocation
    | expression '[' expression ']'
    | expression '(' expressionList? ')'
    | 'new' creator
    | '(' typeType ')' expression
    | expression ('++' | '--')
    | ('+'|'-'|'++'|'--') expression
    | ('~'|'!') expression
    | expression ('*'|'/'|'%') expression
    | expression ('+'|'-') expression
    | expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    | expression ('<=' | '>=' | '>' | '<') expression
    | expression 'instanceof' typeType
    | expression ('==' | '!=') expression
    | expression '&' expression
    | expression '^' expression
    | expression '|' expression
    | expression '&&' expression
    | expression '||' expression
    | expression '?' expression ':' expression
    | expression
      ('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
      expression
    ;

primary
    : '(' expression ')'
    | 'this'
    | 'super'
    | literal
    | Identifier
    | typeTypeOrVoid '.' 'class'
    | 'void' '.' 'class'
    ;

creator
    : nonWildcardTypeArguments createdName classCreatorRest
    | createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    : Identifier typeArgumentsOrDiamond? ('.' Identifier typeArgumentsOrDiamond?)*
    | primitiveType
    ;

innerCreator
    : Identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    : '[' (']' ('[' ']')* arrayInitializer | expression ']' ('[' expression ']')* ('[' ']')*)
    ;

classCreatorRest
    : arguments classBody?
    ;

explicitGenericInvocation
    : nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

typeArgumentsOrDiamond
    : '<' '>'
    | typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    : '<' '>'
    | nonWildcardTypeArguments
    ;

nonWildcardTypeArguments
    : '<' typeList '>'
    ;

typeList
    : typeType (',' typeType)*
    ;

typeTypeOrVoid
    : typeType
    | 'void'
    ;

arguments
    : '(' expressionList? ')'
    ;

expressionList
    : expression (',' expression)*
    ;

// Lexer Rules
IntegerLiteral : [0-9]+ ;
FloatingPointLiteral : [0-9]+ '.' [0-9]* ;
CharacterLiteral : '\'' (~['\\\r\n] | EscapeSequence) '\'' ;
StringLiteral : '"' (~["\\\r\n] | EscapeSequence)* '"' ;
BooleanLiteral : 'true' | 'false' ;
NullLiteral : 'null' ;

Identifier : [a-zA-Z_][a-zA-Z_0-9]* ;

fragment EscapeSequence
    : '\\' [btnfr"'\\]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

WS : [ \t\r\n\u000C]+ -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;

// Add these rules to your existing grammar

block
    : '{' blockStatement* '}'
    ;

blockStatement
    : localVariableDeclarationStatement
    | statement
    ;

statement
    : block
    | 'if' parExpression statement ('else' statement)?
    | 'for' '(' forControl ')' statement
    | 'while' parExpression statement
    | 'do' statement 'while' parExpression ';'
    | 'return' expression? ';'
    | ';'
    | expression ';'
    ;

parExpression
    : '(' expression ')'
    ;

forControl
    : enhancedForControl
    | forInit? ';' expression? ';' forUpdate?
    ;

forInit
    : localVariableDeclaration
    | expressionList
    ;

enhancedForControl
    : variableModifier* typeType variableDeclaratorId ':' expression
    ;

forUpdate
    : expressionList
    ;

localVariableDeclarationStatement
    : localVariableDeclaration ';'
    ;

localVariableDeclaration
    : variableModifier* typeType variableDeclarators
    ;

variableModifier
    : 'final'
    | annotation
    ;

primitiveType
    : 'boolean'
    | 'char'
    | 'byte'
    | 'short'
    | 'int'
    | 'long'
    | 'float'
    | 'double'
    ;

superSuffix
    : arguments
    | '.' Identifier arguments?
    ;

explicitGenericInvocationSuffix
    : 'super' superSuffix
    | Identifier arguments
    ;

annotationTypeBody
    : '{' annotationTypeElementDeclaration* '}'
    ;

annotationTypeElementDeclaration
    : modifier* annotationTypeElementRest
    | ';'
    ;

annotationTypeElementRest
    : typeType annotationMethodOrConstantRest ';'
    | classDeclaration ';'?
    | interfaceDeclaration ';'?
    | enumDeclaration ';'?
    | annotationTypeDeclaration ';'?
    ;

annotationMethodOrConstantRest
    : annotationMethodRest
    | annotationConstantRest
    ;

annotationMethodRest
    : Identifier '(' ')' defaultValue?
    ;

annotationConstantRest
    : variableDeclarators
    ;

defaultValue
    : 'default' elementValue
    ;

// Add these interface-related rules
interfaceDeclaration
    : 'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
    ;

interfaceBody
    : '{' interfaceBodyDeclaration* '}'
    ;

interfaceBodyDeclaration
    : modifier* interfaceMemberDeclaration
    | ';'
    ;

interfaceMemberDeclaration
    : constDeclaration
    | interfaceMethodDeclaration
    | genericInterfaceMethodDeclaration
    | interfaceDeclaration
    | annotationTypeDeclaration
    | classDeclaration
    | enumDeclaration
    ;

constDeclaration
    : typeType constantDeclarator (',' constantDeclarator)* ';'
    ;

constantDeclarator
    : Identifier ('[' ']')* '=' variableInitializer
    ;

// Add enum-related rules
enumDeclaration
    : 'enum' Identifier ('implements' typeList)? enumBody
    ;

enumBody
    : '{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    : enumConstant (',' enumConstant)*
    ;

enumConstant
    : annotation* Identifier arguments? classBody?
    ;

enumBodyDeclarations
    : ';' classBodyDeclaration*
    ;

interfaceMethodDeclaration
    : interfaceMethodModifier* typeTypeOrVoid Identifier formalParameters ('[' ']')*
      ('throws' qualifiedNameList)? ';'
    ;

interfaceMethodModifier
    : annotation
    | 'public'
    | 'abstract'
    | 'default'
    | 'static'
    | 'strictfp'
    ;

genericInterfaceMethodDeclaration
    : typeParameters interfaceMethodDeclaration
    ; 