/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2021 Andreas Veithen
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

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Date;

import org.eclipse.osgi.signedcontent.SignedContent;
import org.eclipse.osgi.signedcontent.SignedContentEntry;
import org.eclipse.osgi.signedcontent.SignerInfo;

final class DummySignedContent implements SignedContent {
    @Override
    public SignedContentEntry[] getSignedEntries() {
        return new SignedContentEntry[0];
    }

    @Override
    public SignedContentEntry getSignedEntry(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignerInfo[] getSignerInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSigned() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getSigningTime(SignerInfo signerInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignerInfo getTSASignerInfo(SignerInfo signerInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkValidity(SignerInfo signerInfo)
            throws CertificateExpiredException, CertificateNotYetValidException {
        throw new UnsupportedOperationException();
    }
}
