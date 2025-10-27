# ModernConnector
Library like J2ME's Connector.open() but with TLS 1.2 automatically.

### Usage 
HTTPS:
```java
// Somewhere
private static byte[] readAll(InputStream in, int max) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buf = new byte[512];
    int total = 0;
    while (true) {
        int want = Math.min(buf.length, max - total);
        if (want <= 0) break;
        int n = in.read(buf, 0, want);
        if (n == -1) break;
        bos.write(buf, 0, n);
        total += n;
    }
    return bos.toByteArray();
}

HttpConnection hc = (HttpConnection) ModernConnector.open("https://cloudflare.com/cdn-cgi/trace");
hc.setRequestMethod(HttpConnection.GET);
hc.setRequestProperty("Accept", "*/*");

// 4) Get code (forces the send), then read body
int code = hc.getResponseCode();
if (code < 200 || code >= 300) {
    throw new IOException("HTTP " + code + " " + hc.getResponseMessage());
}

InputStream in = hc.openInputStream();
String output = new String(readAll(in, Integer.MAX_VALUE));
System.out.println(output);
```

WebSocket:
```java
WebSocketClient ws = (WebSocketClient) ModernConnector.open("wss://gateway.discord.gg/");

Runnable myRunnable;
myRunnable = new Runnable() {
    public void run() {
        while (true) {
            try {
                String message = ws.receiveMessageString(); // ws.receiveMessageBinary to get binary data instead
                System.out.println(message);
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

        }
    }
};

Thread thread = new Thread(myRunnable);
thread.start();
```

TLS:
```
// Too lazy to write this out right now, its the exact same as it would be but replace Connector with ModernConnector
```

### No such class java.*
This library requires that your application use proguard to mangle classes. In the future I'll change the build process so its mangled for you automatically

### The Elephant in the Room
If you want to use ModernConnector for ports 80/443/8080, your app WILL need a code signing signature or some way to bypass codesigning

### Certificate validation
It simply does not exist. This library currently takes care of encryption but will not verify the remote server. It cannot be considered secure for that reason, but it can let you connect to secure services.

### Building
Use netbeans 8.2 with Mobility and the proper j2me sdk setup. If that sounds awful its because it is. I should fix this eventually.

### Source of the Magic Jar's
THe magicalbouncycastlethatworks.jar is directly from a binary distribution built by BouncyCastle themselves, specifically lcrypto-j2me-160.zip. I'm not sure where to still get this file but its probably on archive.org. This is that file. No version of lcrypto-j2me built after version 164 seems to contain TLS. 

As for the bytecode modified version needed for nokia's due to a bug relating to virtualinvoke is provided by https://github.com/shinovon/
