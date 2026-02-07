package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

@Builder
public record ProjectInfoResponseDto(
        @JsonPropertyDescription("Version information of the project. Not Null")
        Version version,

        @JsonPropertyDescription("Timezone configuration for the application and database. Not Null")
        Timezone timezone,

        @JsonPropertyDescription("Current runtime mode of the application (JVM or Native). Example: JVM. Not Null")
        String runtimeMode) {

    @Builder
    public record Version(
            @JsonPropertyDescription("Current version of the application. Example: 1.0.0 - Not Null")
            String current,

            @JsonPropertyDescription("Latest available version of the application. Example: 1.1.0 - Maybe Null")
            String latest,

            @JsonPropertyDescription("Indicates whether the current version is the latest. Example: true - Not Null")
            Boolean isLatest) {}

    @Builder
    public record Timezone(
            @JsonPropertyDescription("Timezone configured for the application. Example: UTC - Not Null")
            String application,

            @JsonPropertyDescription("Timezone configured for the database. Example: UTC - Maybe Null")
            String database) {}
}
