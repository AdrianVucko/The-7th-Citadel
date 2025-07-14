package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.exception.DocumentationGenerationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentationUtils {
    public static void generateDocumentation() {
        String htmlPageStart = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Project Documentation</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f9f9f9;
                            color: #333;
                            padding: 40px;
                        }
                        h1 {
                            text-align: center;
                            margin-bottom: 50px;
                        }
                        .class-block {
                            background-color: #ffffff;
                            border: 1px solid #ddd;
                            padding: 20px;
                            margin-bottom: 30px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                        }
                        .class-name {
                            font-size: 20px;
                            font-weight: bold;
                            color: #2a4d7f;
                            margin-bottom: 15px;
                        }
                        .method {
                            font-family: Consolas, monospace;
                            background-color: #eef6fa;
                            padding: 6px 12px;
                            margin: 4px 0;
                            border-left: 4px solid #2196F3;
                            white-space: pre-wrap;
                         }
                        .constructor {
                            font-family: Consolas, monospace;
                            background-color: #f4f4f4;
                            padding: 8px 12px;
                            margin: 5px 0;
                            border-left: 4px solid #2a4d7f;
                            white-space: pre-wrap;
                        }
                    </style>
                </head>
                <body>
                <h1>Generated Constructor Documentation</h1>
                """;

        String htmlPageEnd = """
        </body>
        </html>
        """;

        StringBuilder htmlBodyBuilder = new StringBuilder();

        String classPath = "./target/classes/";

        try (Stream<Path> walk = Files.walk(Path.of(classPath))) {
            List<Path> classFiles = walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().endsWith("module-info.class"))
                    .toList();

            for (Path path : classFiles) {
                documentClass(htmlBodyBuilder, path);
            }

            String htmlContent = htmlPageStart + htmlBodyBuilder + htmlPageEnd;

            writeDocumentation(htmlContent);
        } catch (IOException | ClassNotFoundException e) {
            throw new DocumentationGenerationException("The documentation wasn't generated!", e);
        }
    }

    private static void writeDocumentation(String htmlContent) throws IOException {
        Path output = Path.of("doc/documentation.html");
        if (Files.notExists(output)) {
            Files.createDirectories(output.getParent());
            Files.createFile(output);
        }
        Files.writeString(output, htmlContent);
    }

    private static void documentClass(StringBuilder htmlBodyBuilder, Path path) throws ClassNotFoundException {
        String fullClassName = path.toString().substring(17, path.toString().length() - 6)
                .replace(File.separator, ".");

        Class<?> clazz = Class.forName(fullClassName);
        Constructor<?>[] constructors = clazz.getConstructors();

        htmlBodyBuilder.append("<div class=\"class-block\">")
                .append("<div class=\"class-name\">").append(clazz.getName()).append("</div>");

        addConstructors(htmlBodyBuilder, clazz, constructors);
        addMethods(htmlBodyBuilder, clazz);

        htmlBodyBuilder.append("</div>");
    }

    private static void addConstructors(StringBuilder htmlBodyBuilder, Class<?> clazz, Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            StringBuilder params = new StringBuilder();
            Parameter[] parameters = constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                params.append(p.getType().getSimpleName()).append(" ").append(p.getName());
                if (i < parameters.length - 1) params.append(", ");
            }

            htmlBodyBuilder.append("<div class=\"constructor\">")
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(clazz.getSimpleName())
                    .append("(").append(params).append(")")
                    .append("</div>");
        }
    }

    private static void addMethods(StringBuilder htmlBodyBuilder, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() == Object.class) {
                // toString, equals...
                continue;
            }
            if (Modifier.isPublic(method.getModifiers())) {
                StringBuilder params = new StringBuilder();
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    params.append(parameters[i].getType().getSimpleName())
                            .append(" ")
                            .append(parameters[i].getName());
                    if (i < parameters.length - 1) params.append(", ");
                }

                htmlBodyBuilder.append("<div class=\"method\">")
                        .append(Modifier.toString(method.getModifiers())).append(" ")
                        .append(method.getReturnType().getSimpleName()).append(" ")
                        .append(method.getName()).append("(").append(params).append(")")
                        .append("</div>");
            }
        }
    }
}
