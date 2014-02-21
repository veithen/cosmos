package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.io.IOException;

import org.apache.maven.wagon.TransferFailedException;

public interface Resource {
    void fetchTo(File destination) throws TransferFailedException, IOException;
}
