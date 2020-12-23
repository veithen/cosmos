/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2020 Andreas Veithen
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
package com.github.veithen.cosmos.equinox.signedcontent;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.eclipse.osgi.signedcontent.SignedContent;
import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.osgi.framework.Bundle;

public final class DummySignedContentFactory implements SignedContentFactory {
    @Override
    public SignedContent getSignedContent(File content)
            throws IOException, InvalidKeyException, SignatureException, CertificateException,
                    NoSuchAlgorithmException, NoSuchProviderException {
        return new DummySignedContent();
    }

    @Override
    public SignedContent getSignedContent(Bundle bundle)
            throws IOException, InvalidKeyException, SignatureException, CertificateException,
                    NoSuchAlgorithmException, NoSuchProviderException {
        throw new UnsupportedOperationException();
    }
}
