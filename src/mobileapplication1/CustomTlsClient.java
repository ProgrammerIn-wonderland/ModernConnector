/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mobileapplication1;

import java.io.IOException;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsCredentials;

/**
 *
 * @author alice
 */
public class CustomTlsClient extends DefaultTlsClient {

    public TlsAuthentication getAuthentication() throws IOException {
        return new TlsAuthentication() {
            public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
                // Implement certificate validation logic here
                System.out.println("Server certificate received: " + serverCertificate);
            }

            public TlsCredentials getClientCredentials() throws IOException {
                return null; // No client credentials for simplicity
            }

            public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
}