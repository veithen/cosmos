package com.github.veithen.cosmos.wagon;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;

/**
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="p2"
 *                   instantiation-strategy="per-lookup"
 */
public class P2Wagon extends StreamWagon {

    @Override
    public void fillInputData(InputData inputData) throws TransferFailedException,
            ResourceDoesNotExistException, AuthorizationException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fillOutputData(OutputData outputData) throws TransferFailedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void closeConnection() throws ConnectionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void openConnectionInternal() throws ConnectionException, AuthenticationException {
        // TODO Auto-generated method stub
        
    }

}
