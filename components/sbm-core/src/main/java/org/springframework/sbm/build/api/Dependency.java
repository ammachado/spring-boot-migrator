/*
 * Copyright 2021 - 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sbm.build.api;

import io.micrometer.core.lang.Nullable;
import lombok.*;
import org.openrewrite.semver.LatestRelease;
import org.springframework.sbm.SbmConstants;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.sbm.SbmConstants.LS;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dependency {

    @NotNull
    @EqualsAndHashCode.Include
    private String groupId;

    @NotNull
    @EqualsAndHashCode.Include
    private String artifactId;

    /**
     * OpenRewrite expects version to be set and ignores it if version is managed through dependencyManagement.
     */
    @Nullable
    private String version;

    private String type;

    private String scope;

    private String classifier;

    @Builder.Default
    private List<Dependency> exclusions = new ArrayList<>();

    @Override
    public String toString() {
        return "<dependency>" + LS +
                "   <groupId>" + groupId + "</groupId>" + LS +
                "   <artifactId>" + artifactId + "</artifactId>" + LS +
                tagString("version", version) +
                tagString("type", type) +
                tagString("scope", scope) +
                tagString("classifier", classifier) +
                exclusionString() +
                "</dependency>";
    }

    public boolean isRecentThen(Dependency that){
        return this.equals(that) && comparator().compare(this, that) >= 0;
    }

    private Comparator<Dependency> comparator(){
        LatestRelease latestRelease = new LatestRelease(null);
        return Comparator.comparing(Dependency::getVersion, latestRelease);
    }

    private String exclusionString() {
        if (exclusions.isEmpty()) {
            return "";
        } else {
            String b = "   <exclusions>%n".formatted() +
                    exclusions.stream().map(Dependency::toString).collect(Collectors.joining(SbmConstants.LS)) +
                    "   </exclusions>%n".formatted();
            return b;
        }
    }

    private static String tagString(String name, String value) {
        return value == null
                ? ""
                : "   <%1$s>%2$s</%1$s>%n".formatted(name, value);
    }

    /**
     * @return the dependency coordinates as {@code 'groupId:artifactId:version'}
     */
    public String getCoordinates() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    public static Dependency fromCoordinates(String coordinate) {
        String[] splitCoordinates = coordinate.split(":");

        if (splitCoordinates.length == 2) {
            return Dependency.builder()
                    .groupId(splitCoordinates[0])
                    .artifactId(splitCoordinates[1]).build();
        } else if (splitCoordinates.length == 3) {
            return Dependency.builder()
                    .groupId(splitCoordinates[0])
                    .artifactId(splitCoordinates[1])
                    .version(splitCoordinates[2]).build();
        } else if (splitCoordinates.length == 4) {
            return Dependency.builder()
                    .groupId(splitCoordinates[0])
                    .artifactId(splitCoordinates[1])
                    .version(splitCoordinates[2])
                    .classifier(splitCoordinates[3]).build();
        } else {
            throw new IllegalArgumentException("Expected dependency in format groupid:artifactid[:version], but it is: " + coordinate);
        }
    }
}
