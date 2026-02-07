package br.com.gorillaroxo.sanjy.client.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticVersioningComparatorService {

    /**
     * Compares two semantic versioning strings to determine their ordering.
     *
     * @param currentVersion the current version to compare
     * @param targetVersion the target version to compare against
     * @return a negative integer if {@code currentVersion} is lower than {@code targetVersion}, zero if they are equal,
     *     or a positive integer if {@code currentVersion} is greater than {@code targetVersion}
     */
    public int compare(String currentVersion, String targetVersion) {
        final var comparableCurrent = new ComparableVersion(currentVersion);
        final var comparableTarget = new ComparableVersion(targetVersion);

        return comparableCurrent.compareTo(comparableTarget);
    }
}
