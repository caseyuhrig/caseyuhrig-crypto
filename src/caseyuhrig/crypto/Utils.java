package caseyuhrig.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;


public class Utils
{
    public static Logger initLogger()
    {
        try
        {
            final var logConfigFileName = "log4j2.xml";
            final var file = new File(".").toPath().normalize().toFile();
            final var logConfigFile = file.toPath().resolve(logConfigFileName).normalize().toFile();
            if (logConfigFile.exists() == true)
            {
                try (final var in = new FileInputStream(logConfigFile))
                {
                    Configurator.initialize(null, new ConfigurationSource(in));
                }
                final var logger = LogManager.getLogger(Main.class);
                logger.info("LOGGING INIT: file: " + logConfigFile.getAbsolutePath());
                return logger;
            }
            else
            {
                final var resourcePath = String.format("/caseyuhrig/crypto/%s", logConfigFileName);
                try (final var logInputStream = Main.class.getResourceAsStream(resourcePath))
                {
                    Configurator.initialize(null, new ConfigurationSource(logInputStream));
                }
                final var logger = LogManager.getLogger(Main.class);
                logger.info("LOGGING INIT: resource: " + resourcePath);
                return logger;
            }
        }
        catch (final Throwable t)
        {
            throw new RuntimeException(t.getLocalizedMessage(), t);
        }
    }


    public static boolean isEmpty(final String value)
    {
        return value == null || "".equals(value);
    }
}
