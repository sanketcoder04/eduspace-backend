package com.example.eduspace.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "eduspace.storage")
public class StorageProperties {

    /** Project URL from Supabase dashboard, e.g. https://xxxxxxxx.supabase.co */
    private String url;

    /** Settings > API > service_role key (server-side only — never expose to frontend). */
    private String serviceRoleKey;

    /** Storage bucket name, must be created + marked Public in the dashboard. */
    private String bucket;

    private long maxFileSizeMb = 10;
}