package org.apache.avro.mojo;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProtocolMojoTest {

    private final ProtocolMojo mojo = new ProtocolMojo();

    private final String tmp = System.getProperty("java.io.tmpdir");

    @Before
    public void setup() {
        cleanup();
        mojo.sourceDirectory = new File("src/test/resources/input");
        mojo.outputDirectory = new File(tmp);
        mojo.schemaExtension = ProtocolMojo.SCHEMA_EXTENSION;
        mojo.protocolExtension = ProtocolMojo.PROTOCOL_EXTENSION;
        mojo.project = new MavenProject();
    }

    @After
    public void after() {
        cleanup();
    }

    @Test
    public void testExecute() throws MojoExecutionException {
        mojo.execute();
        testExecuteIdl();
        testExecuteAvpr();
    }

    private void cleanup() {
        ProtocolMojo.deleteDir(new File(tmp, "mynamespace"));
        ProtocolMojo.deleteDir(new File(tmp, "mynamespaceavpr"));
    }

    private void testExecuteIdl() throws MojoExecutionException {
        File f = new File(tmp, "mynamespace");
        assertTrue("There's no output directory " + f.getAbsolutePath(),
                f.isDirectory());
        assertTrue("There are not files in " + f.getAbsolutePath(),
                f.list().length > 0);
        File java = new File(f, "MyProtocol.java");
        assertTrue("There's no compiled java file at " + java.getAbsolutePath(),
                java.isFile());
    }

    private void testExecuteAvpr() throws MojoExecutionException {
        File f = new File(tmp, "mynamespaceavpr");
        assertTrue("There's no output directory " + f.getAbsolutePath(),
                f.isDirectory());
        assertTrue("There are not files in " + f.getAbsolutePath(),
                f.list().length > 0);
        File java = new File(f, "MyProtocolAvpr.java");
        assertTrue("There's no compiled java file at " + java.getAbsolutePath(),
                java.isFile());
    }

}
