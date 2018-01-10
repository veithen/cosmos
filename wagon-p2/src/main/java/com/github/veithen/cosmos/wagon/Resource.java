package com.github.veithen.cosmos.wagon;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.wagon.TransferFailedException;

public interface Resource {
    void fetchTo(OutputStream out) throws TransferFailedException, IOException;
}
