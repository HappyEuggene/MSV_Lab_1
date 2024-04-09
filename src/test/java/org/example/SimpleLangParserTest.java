package org.example;

import org.example.SimpleLangParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SimpleLangParserTest {
    private SimpleLangParser parser;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        parser = new SimpleLangParser();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void testAssignmentAndPrint() {
        String code = "x = 10;\nprint x;";
        parser.execute(code);
        assertEquals("10\r\n", outputStream.toString(), "Print output should be '10'");
    }

    @Test
    public void testFunctionExecution() {
        String code = "func add() { result = 3 + 2; }\nadd();\nprint result;";
        parser.execute(code);
        assertEquals("5\r\n", outputStream.toString(), "Print output should be '5'");
    }

    @Test
    public void testInvalidSyntaxThrowsException() {
        String code = "func add() {";
        assertThrows(IllegalArgumentException.class, () -> parser.execute(code), "Should throw IllegalArgumentException for invalid syntax");
    }


    @Test
    public void testUndefinedFunctionCall() {
        String code = "callUndefinedFunction();";
        parser.execute(code);
        assertEquals("Function callUndefinedFunction is not defined\r\n", outputStream.toString(), "Should print an undefined function error");
    }

    @ParameterizedTest
    @ValueSource(strings = { "5 + 5", "2 * 3", "10 - 5", "20 / 4" })
    public void testArithmeticOperations(String operation) {
        String code = String.format("result = %s;\nprint result;", operation);
        parser.execute(code);
        assertThat(outputStream.toString(), not(isEmptyString()));
        assertThat(Integer.parseInt(outputStream.toString().trim()), greaterThan(0));
    }


    @Test
    public void testInvalidSyntax() {
        String code = "x = 5 + ;";
        assertThrows(IllegalArgumentException.class, () -> parser.execute(code), "Should throw IllegalArgumentException for invalid syntax");
    }

    @ParameterizedTest
    @ValueSource(strings = { "15 + 5", "20 - 5", "4 * 5", "20 / 2" })
    public void testMultipleArithmeticOperations(String operation) {
        String code = String.format("result = %s;\nprint result;", operation);
        parser.execute(code);
        assertThat(outputStream.toString(), not(containsString("Error")));
        assertThat(outputStream.toString().trim(), not(isEmptyString()));
    }

    @Test
    public void testUndefinedVariableThrowsException() {
        String code = "print a;";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.execute(code));
        assertTrue(exception.getMessage().contains("Variable a is not defined"));
    }

    @Test
    public void testArithmeticOperations() {
        String code = "x = 10 + 5;\nprint x;";
        parser.execute(code);
        assertEquals("15\r\n", outputStream.toString());
    }

    @Test
    public void testDivisionByZeroThrowsException() {
        String code = "x = 10 / 0;";
        assertThrows(UnsupportedOperationException.class, () -> parser.execute(code));
    }

    @Test
    public void testMathOperationsWithParentheses() {
        String code = "result = (10 + 5) * 2;\nprint result;";
        parser.execute(code);
        assertEquals("30\r\n", outputStream.toString());
    }

    @Test
    public void testVariableReassignment() {
        String code = "x = 10;\nx = 20;\nprint x;";
        parser.execute(code);
        assertEquals("20\r\n", outputStream.toString());
    }
}
