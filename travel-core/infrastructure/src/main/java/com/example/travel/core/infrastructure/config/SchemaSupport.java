package com.example.travel.core.infrastructure.config;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

/**
 * Remove auto-generated schema files before JPA generates them. Otherwise new content will be
 * appended to the files.
 */
@Configuration
@Profile("!test")
public class SchemaSupport {

    @Bean
    public BeanFactoryPostProcessor schemaFilesCleanupPostProcessor() {
        return bf -> {
            delete("travel-core/infrastructure/src/main/resources/db/schema_create.sql");
            delete("travel-core/infrastructure/src/main/resources/db/schema_drop.sql");
            delete("src/main/resources/db/schema_create.sql");
            delete("src/main/resources/db/schema_drop.sql");
        };
    }

    private void delete(String resourceLocation) {
        try {
            FileUtils.forceDelete(ResourceUtils.getFile(resourceLocation));
        } catch (IOException ignored) {
            //ignore
        }
    }
}
