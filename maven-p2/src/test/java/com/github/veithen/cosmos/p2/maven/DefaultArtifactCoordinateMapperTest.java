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

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.equinox.p2.metadata.Version;
import org.junit.Before;
import org.junit.Test;

public class DefaultArtifactCoordinateMapperTest {
    private ArtifactCoordinateMapper mapper;

    @Before
    public void setUp() {
        mapper = new DefaultArtifactCoordinateMapper();
    }

    @Test
    public void testCreateIArtifactKey() {
        P2Coordinate p2Coordinate = mapper.createP2Coordinate(new DefaultArtifact("osgi.bundle", "mybundle", "jar", "2.1.0"));
        assertThat(p2Coordinate.getClassifier()).isEqualTo("osgi.bundle");
        assertThat(p2Coordinate.getId()).isEqualTo("mybundle");
        assertThat(p2Coordinate.getVersion()).isEqualTo(Version.createOSGi(2, 1, 0));
    }
}