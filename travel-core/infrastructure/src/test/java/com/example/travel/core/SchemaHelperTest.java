package com.example.travel.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
class SchemaHelperTest {

    public static final String SEQUENCES = "V1_0_0__Create-Sequences.sql";
    public static final String CREATE_TABLES = "V1_0_1__Create-Tables.sql";
    public static final String SYSTEM_USER = "V1_0_2__Create-System-User.sql";
    public static final String ALTER_TABLES = "V1_0_3__Alter-Tables.sql";

    @Test
    @Disabled
    void createInitialScripts() throws Exception {
        URI schemaCreate = this.getClass().getClassLoader().getResource("db/schema_create.sql").toURI();

        Map<String, List<String>> contentMap = new LinkedHashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get(schemaCreate), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                if (Strings.CS.startsWith(line, "create sequence")) {
                    putToMap(contentMap, SEQUENCES, line);
                } else if (Strings.CS.startsWith(line, "create table")) {
                    putToMap(contentMap, CREATE_TABLES, line);
                } else if (Strings.CS.startsWith(line, "alter table")) {
                    putToMap(contentMap, ALTER_TABLES, line);
                }
            });
        }
        putToMap(contentMap, SYSTEM_USER,
                "INSERT INTO app_user (id, version, created_date, last_modified_date, username) VALUES (nextval('app_user_seq'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM');");

        write(SEQUENCES, contentMap.get(SEQUENCES));
        write(CREATE_TABLES, contentMap.get(CREATE_TABLES));
        write(SYSTEM_USER, contentMap.get(SYSTEM_USER));
        write(ALTER_TABLES, contentMap.get(ALTER_TABLES));
    }

    private void write(String key, List<String> lines) {
        delete("src/main/resources/db/migration/postgres/" + key);
        create("src/main/resources/db/migration/postgres/" + key, lines);
    }

    private static void putToMap(Map<String, List<String>> contentMap, String mapKey, String line) {
        List<String> content = contentMap.computeIfAbsent(mapKey, s -> new ArrayList<>());
        content.add(line);
    }

    private void delete(String resourceLocation) {
        try {
            FileUtils.forceDelete(
                    ResourceUtils.getFile(resourceLocation));
        } catch (IOException e) {
            //ignore
        }
    }

    private void create(String resourceLocation, List<String> lines) {
        try {
            FileUtils.writeLines(new File(resourceLocation), "UTF-8", lines);
        } catch (IOException e) {
            //ignore
        }
    }

}
