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

    @Test
    public void invokingMainWithOneArgumentPrintsUsage()
    {
        final String output = invokeMain(new String[] {"foo.txt"});
        assertTrue(output.startsWith("Usage:"));
    }

    @Test
    public void invokingMainWithInvalidSpecFilePrintsError()
    {
        final String output = invokeMain(new String[] {"foo.txt", "bar.txt"});
        assertTrue(output.startsWith("Invalid file path:"));
    }

    private String invokeMain(final String[] args)
    {
        MiniREScanner.main(args);
        return outputStream.toString();
    }

}
