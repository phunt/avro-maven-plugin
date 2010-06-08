/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avro.mojo;

import org.apache.avro.specific.SpecificCompiler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.io.IOException;

/**
 * Compile an Avro protocol schema file.
 *
 * @goal protocol
 * @phase generate-sources
 */
public class ProtocolMojo extends AbstractMojo {
    /**
     * @parameter expression="${sourceDirectory}" default-value="${basedir}/src/main/avro"
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${outputDirectory}" default-value="${project.build.directory}/generated-sources/avro"
     */
    private File outputDirectory;

    /**
     * A set of Ant-like inclusion patterns used to select files from
     * the source directory for processing. By default, the pattern
     * <code>**&#47;*.avro</code> is used to select grammar files.
     *
     * @parameter
     */
    private String[] includes = new String[]{"**/*.avpr", "**/*.avsc"};

    /**
     * A set of Ant-like exclusion patterns used to prevent certain
     * files from being processed. By default, this set is empty such
     * that no files are excluded.
     *
     * @parameter
     */
    private String[] excludes = new String[0];

    /**
     * The current Maven project.
     *
     * @parameter default-value="${project}"
     * @readonly
     * @required
     */
    private MavenProject project;

    /**
     * @parameter default-value="false"
     */
    private boolean schemas;

    private FileSetManager fileSetManager = new FileSetManager();

    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.isDirectory()) {
            throw new MojoExecutionException(sourceDirectory
                    + "is not a directory");
        }

        FileSet fs = new FileSet();
        fs.setDirectory(sourceDirectory.getAbsolutePath());
        fs.setFollowSymlinks(false);

        for (String include : includes) {
            fs.addInclude(include);
        }
        for (String exclude : excludes) {
            fs.addExclude(exclude);
        }

        String[] includedFiles = fileSetManager.getIncludedFiles(fs);

        for (String filename : includedFiles) {
            try {
                if (schemas || filename.endsWith(".avsc")) {
                    SpecificCompiler.compileSchema(
                            new File(sourceDirectory, filename),
                            outputDirectory);
                } else {
                    SpecificCompiler.compileProtocol(
                            new File(sourceDirectory, filename),
                            outputDirectory);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Error compiling protocol file "
                        + filename + " to " + outputDirectory, e);
            }
        }

        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }
}
