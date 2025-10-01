/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mobileapplication1;

import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author NealShah
 */
public class ModernConnector {
    public static Object open(String connectionURL) throws IOException{
        String protocol = Utils.split(connectionURL, ':')[0];
        if (protocol.equals("ssl") || protocol.equals("tls")) {
            String endpoint = Utils.split(connectionURL.substring(5), '/')[0];
            String[] endpointData = Utils.split(endpoint, ':');
            String host = endpointData[0];
            int port = Integer.parseInt(endpointData[1]);
            
            return new ModernSecureConnection(host, port);
        }
        if (protocol.equals("ws")) {
            
        }
        if (protocol.equals("wss")) {
            
        }
        
        return Connector.open(connectionURL);
    }

}
