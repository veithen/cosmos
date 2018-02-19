/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.p2.maven;

import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.DefaultArtifactCoordinate;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

@Component(role=ArtifactCoordinateMapper.class)
public class DefaultArtifactCoordinateMapper implements ArtifactCoordinateMapper {
    @Override
    public ArtifactCoordinate createArtifactCoordinate(IArtifactKey artifactKey) {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();
        coordinate.setGroupId(artifactKey.getClassifier());
        String id = artifactKey.getId();
        if (id.endsWith(".source")) {
            coordinate.setArtifactId(id.substring(0, id.length()-7));
            coordinate.setClassifier("sources");
        } else {
            coordinate.setArtifactId(id);
        }
        coordinate.setVersion(artifactKey.getVersion().toString());
        coordinate.setExtension("jar");
        return coordinate;
    }

    @Override
    public IArtifactKey createIArtifactKey(IArtifactRepository artifactRepository,
            ArtifactCoordinate artifactCoordinate) {
        String id;
        String classifier = artifactCoordinate.getClassifier();
        if (classifier == null) {
            id = artifactCoordinate.getArtifactId();
        } else if (classifier.equals("sources")) {
            id = artifactCoordinate.getArtifactId() + ".source";
        } else {
            return null;
        }
        return artifactRepository.createArtifactKey(
                artifactCoordinate.getGroupId(), id, Version.create(artifactCoordinate.getVersion()));
    }

    @Override
    public ArtifactKeyQuery createArtifactKeyQuery(String groupId, String artifactId) {
        return new ArtifactKeyQuery(groupId, artifactId, null);
    }
}
