package de.glaubekeinemdev.discordutilities.discordlogger;

import jline.console.ConsoleReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.*;

public class DiscordBotLogger extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final LoggingFormatter formatter = new LoggingFormatter();
    private ConsoleReader reader;
    private final String name = System.getProperty("user.name");
    private final String prompt;

    public DiscordBotLogger(final String prompt, final boolean changeDefaultCharset) {
        super("DiscordBotLogger", null);

        this.prompt = name + "@" + prompt + " $ ";

        try {
            if(changeDefaultCharset) {
                System.err.close();

                final Field field = Charset.class.getDeclaredField("defaultCharset");
                field.setAccessible(true);
                field.set(null, StandardCharsets.UTF_8);

                System.setErr(System.out);
            }

            if(!Files.exists(Paths.get("logs"))) {
                Files.createDirectory(Paths.get("logs"));
            }

            setLevel(Level.ALL);

            this.reader = new ConsoleReader(System.in, System.out);
            this.reader.setExpandEvents(false);

            final LoggingHandler loggingHandler = new LoggingHandler();
            loggingHandler.setFormatter(formatter);
            loggingHandler.setEncoding(StandardCharsets.UTF_8.name());
            loggingHandler.setLevel(Level.INFO);
            addHandler(loggingHandler);

            final FileHandler fileHandler = new FileHandler("logs/latest.log", 8000000, 8, true);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setFormatter(new LoggingFormatter());
            addHandler(fileHandler);


            System.setOut(new AsyncPrintStream(new LoggingOutputStream(Level.INFO)));
            System.setErr(new AsyncPrintStream(new LoggingOutputStream(Level.SEVERE)));

            this.reader.setPrompt(prompt);
            this.reader.resetPromptLine(prompt, "", 0);
        } catch(IOException | NoSuchFieldException | IllegalAccessException e) {
            e.fillInStackTrace();
            e.printStackTrace();
        }

    }

    public LoggingFormatter getFormatter() {
        return formatter;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    public ConsoleReader getReader() {
        return reader;
    }


    public String readLine() {
        try {
            String line = this.reader.readLine();
            this.reader.setPrompt(prompt);
            return line;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Shuts down all handlers and the reader.
     */
    public void shutdownAll() {
        for(Handler handler : getHandlers()) {
            handler.close();
        }
        try {
            this.reader.killLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output stream that sends the last message in the buffer to the log handlers
     * when flushed.
     */
    private class LoggingOutputStream extends ByteArrayOutputStream {
        private final Level level;

        public LoggingOutputStream(Level level) {
            this.level = level;
        }

        @Override
        public void flush() throws IOException {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if(!contents.isEmpty() && !contents.equals(separator)) {
                logp(level, "", "", contents);
            }
        }
    }

    private class LoggingHandler extends Handler {

        private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");

        private boolean closed;

        @Override
        public void publish(LogRecord record) {
            if(closed) {
                return;
            }

            if(isLoggable(record)) {
                try {
                    StringBuilder builder = new StringBuilder();

                    if(record.getThrown() != null) {
                        StringWriter writer = new StringWriter();
                        record.getThrown().printStackTrace(new PrintWriter(writer));
                        builder.append(writer).append('\n');
                    }

                    final String formattedMessage = "[" + format.format(record.getMillis()) + "] " + record.getLevel()
                            .getName() + ": " + formatMessage(
                            record) + '\n' + builder.toString();

                    reader.print(ConsoleReader.RESET_LINE + formattedMessage);
                    reader.drawLine();
                    reader.flush();
                } catch(Throwable ignored) {
                }
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
            closed = true;
        }

        public String formatMessage(LogRecord record) {
            String format = record.getMessage();
            ResourceBundle catalog = record.getResourceBundle();
            if(catalog != null) {
                try {
                    format = catalog.getString(format);
                } catch(MissingResourceException var8) {
                }
            }

            try {
                Object[] parameters = record.getParameters();
                if(parameters != null && parameters.length != 0) {
                    int index = -1;
                    int fence = format.length() - 1;

                    while((index = format.indexOf(123, index + 1)) > -1 && index < fence) {
                        char digit = format.charAt(index + 1);
                        if(digit >= '0' & digit <= '9') {
                            return MessageFormat.format(format, parameters);
                        }
                    }

                    return format;
                } else {
                    return format;
                }
            } catch(Exception var9) {
                return format;
            }
        }
    }

    private static class LoggingFormatter extends Formatter {

        private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            if(record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append('\n');
            }

            return ConsoleReader.RESET_LINE + "[" + format.format(record.getMillis()) + "] " + record.getLevel()
                    .getName() + ": " + formatMessage(
                    record) + '\n' + builder.toString();
        }
    }

}
