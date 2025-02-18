package com.tonic.demo;

import com.tonic.codegen.TLangCompiler;
import com.tonic.utill.BytecodeExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args)
    {
        TLangCompiler compiler = new TLangCompiler();

        try (
                InputStream is = Main.class.getResourceAsStream("demo.tlang");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String name = UUID.randomUUID().toString();
            String sourceCode = reader.lines().collect(Collectors.joining("\n"));
            byte[] bytecode = compiler.compile(name, sourceCode);
            try {
                // Write the byte array to the file
                Path path = Paths.get("C:\\test\\new\\" + name + ".class");
                Files.write(path, bytecode);
                System.out.println("File written successfully to: " + path.toAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            BytecodeExecutor.execute(name, bytecode);

        } catch (Exception e) {
            System.err.println("Error compiling example script:");
            e.printStackTrace();
        }
    }
}