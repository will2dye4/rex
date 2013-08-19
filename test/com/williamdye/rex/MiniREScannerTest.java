package com.williamdye.rex;

import org.junit.*;

import java.io.*;

import static org.junit.Assert.*;

public class MiniREScannerTest
{

    private static final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeClass
    public static void setUpOutputStream()
    {
        System.setOut(new PrintStream(outputStream));
    }

    @Before
    public void resetOutputStream()
    {
        outputStream.reset();
    }

    @Test
    public void invokingMainWithOneArgumentPrintsUsage()
    {
        final String output = invokeMain("foo.txt");
        assertTrue(output.startsWith("Usage:"));
    }

    @Test
    public void invokingMainWithInvalidSpecFilePrintsError()
    {
        final String output = invokeMain("foo.txt", "bar.txt");
        assertTrue(output.startsWith("Invalid file path:"));
    }

    private String invokeMain(String... args)
    {
        MiniREScanner.main(args);
        String output = outputStream.toString();
        if (output.startsWith("Welcome"))
            output = output.substring(output.indexOf('\n') + 1);
        return output;
    }

}
