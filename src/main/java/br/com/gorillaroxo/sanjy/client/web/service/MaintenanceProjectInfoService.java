package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client.MaintenanceRestClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ProjectInfoMaintenanceControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.util.DetectRuntimeMode;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceProjectInfoService {

    private final SanjyClientWebConfigProp prop;
    private final GetLatestProjectVersionService getLatestProjectVersionService;
    private final SemanticVersioningComparatorService semanticVersioningComparatorService;
    private final MaintenanceRestClient maintenanceRestClient;

    public ProjectInfoMaintenanceControllerResponseDto execute() {
        final var sanjyClientWeb = buildSanjyClientWeb();
        final var sanjyServer = buildSanjyServer();

        return ProjectInfoMaintenanceControllerResponseDto.builder()
            .sanjyServer(sanjyServer)
            .sanjyClientWeb(sanjyClientWeb)
            .build();
    }

    private ProjectInfoMaintenanceControllerResponseDto.Project buildSanjyClientWeb() {
        final var version = buildVersionSanjyClientWeb();
        final var runtimeMode = DetectRuntimeMode.detect();

        return ProjectInfoMaintenanceControllerResponseDto.Project.builder()
            .version(version)
            .runtimeMode(runtimeMode)
            .build();
    }

    private ProjectInfoMaintenanceControllerResponseDto.Project buildSanjyServer() {
        final ProjectInfoResponseDto projectInfo = maintenanceRestClient.projectInfo();
        final ProjectInfoResponseDto.Version version = projectInfo.version();

        return ProjectInfoMaintenanceControllerResponseDto.Project.builder()
            .runtimeMode(projectInfo.runtimeMode())
            .version(ProjectInfoMaintenanceControllerResponseDto.Version.builder()
                .current(version.current())
                .latest(version.latest())
                .isLatest(version.isLatest())
                .build())
            .build();
    }

    private ProjectInfoMaintenanceControllerResponseDto.Version buildVersionSanjyClientWeb() {
        final var current = prop.application().version();
        final var latest = fetchLatestVersionFromGitHub();

        final BooleanSupplier getIsLatest = () -> {
            if (Objects.isNull(latest)) {
                return true;
            }

            final var comparatorResult = semanticVersioningComparatorService.compare(current, latest);
            return comparatorResult >= 0;
        };

        return ProjectInfoMaintenanceControllerResponseDto.Version.builder()
            .current(current)
            .latest(latest)
            .isLatest(getIsLatest.getAsBoolean())
            .build();
    }


    private String fetchLatestVersionFromGitHub() {
        try {
            return Optional.ofNullable(getLatestProjectVersionService.clientWeb())
                .filter(Predicate.not(String::isBlank))
                .orElse(null);
        } catch (final Exception e) {
            log.warn(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Error fetching latest version from GitHub"),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);
            return null;
        }
    }
}
