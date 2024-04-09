package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleLangParser {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, String> functions = new HashMap<>();

    public void execute(String code) {
        String[] lines = code.split("\n");
        StringBuilder currentBlock = new StringBuilder();
        boolean inFunction = false;
        int openBraces = 0;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("func")) {
                inFunction = true;
            }

            if (inFunction) {
                currentBlock.append(line).append("\n");

                if (line.contains("{")) {
                    openBraces++;
                }
                if (line.contains("}")) {
                    openBraces--;
                }

                if (openBraces == 0) {
                    handleFunctionDefinition(currentBlock.toString());
                    inFunction = false;
                    currentBlock = new StringBuilder();
                }
            } else if (!line.isEmpty()) {
                processStatement(line);
            }
        }

        if (inFunction) {
            throw new IllegalArgumentException("Unclosed function definition");
        }
    }

    private void processStatement(String statement) {
        statement = statement.trim();
        if (statement.contains("=")) {
            handleAssignment(statement);
        } else if (statement.startsWith("print")) {
            handlePrint(statement);
        } else if (statement.endsWith("();")) {
            handleFunctionCall(statement);
        }
    }

    private void handleAssignment(String statement) {
        String[] parts = statement.split("=");
        String variableName = parts[0].trim();
        int value = evaluateExpression(parts[1].trim());
        variables.put(variableName, value);
    }

    private void handlePrint(String statement) {
        String expression = statement.substring(6).trim();
        int result = evaluateExpression(expression);
        System.out.println(result);
    }

    private void handleFunctionDefinition(String statement) {
        int funcNameStartIndex = statement.indexOf("func") + "func".length();
        int funcNameEndIndex = statement.indexOf("(", funcNameStartIndex);
        if (funcNameEndIndex == -1) {
            throw new IllegalArgumentException("Invalid function declaration");
        }

        String funcName = statement.substring(funcNameStartIndex, funcNameEndIndex).trim();
        int bodyStartIndex = statement.indexOf("{", funcNameEndIndex);
        int bodyEndIndex = statement.lastIndexOf("}");
        if (bodyStartIndex == -1 || bodyEndIndex == -1) {
            throw new IllegalArgumentException("Invalid function body");
        }

        String body = statement.substring(bodyStartIndex + 1, bodyEndIndex).trim();
        functions.put(funcName, body);
    }



    private void handleFunctionCall(String statement) {
        String funcName = statement.contains("(") ? statement.substring(0, statement.indexOf("(")).trim() : statement.trim();

        if (functions.containsKey(funcName)) {
            String functionBody = functions.get(funcName);
            execute(functionBody);
        } else {
            System.out.println("Function " + funcName + " is not defined");
        }
    }

    private int evaluateExpression(String expression) {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(variables);
        return evaluator.evaluate(expression);
    }
}
