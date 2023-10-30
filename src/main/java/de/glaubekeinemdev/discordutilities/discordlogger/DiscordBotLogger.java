package de.glaubekeinemdev.discordutilities.discordlogger;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscordBotLogger {

        private static final String LOG_FOLDER = "logs";
        private static final String LOG_FILE = "log.txt";
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - hh:mm:ss");

        public DiscordBotLogger() {
            redirectSystemStreams();
        }

        public static void redirectSystemStreams() {
            System.setOut(new LoggingPrintStream(System.out, false));
            System.setErr(new LoggingPrintStream(System.err, true));
        }

        private static class LoggingPrintStream extends PrintStream {
            private final boolean isErrorStream;

            public LoggingPrintStream(PrintStream original, boolean isErrorStream) {
                super(original);
                this.isErrorStream = isErrorStream;
            }

            @Override
            public void println(String message) {
                String formattedMessage = "[" + DATE_FORMAT.format(new Date()) + "] " +
                        (isErrorStream ? "ERROR: " : "") + message;
                super.println(formattedMessage);
                writeToFile(formattedMessage);
            }
        }

        private static void writeToFile(String message) {
            Path logFolder = Paths.get(LOG_FOLDER);
            if (!Files.exists(logFolder)) {
                try {
                    Files.createDirectory(logFolder);
                } catch (IOException e) {
                    System.err.println("Could not create log folder");
                    return;
                }
            }
            Path logFile = logFolder.resolve(LOG_FILE);
            try {
                Files.write(logFile, (message + "\n").getBytes(),
                        Files.exists(logFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            } catch (IOException e) {
                System.err.println("Could not write to log file");
            }
        }
    }