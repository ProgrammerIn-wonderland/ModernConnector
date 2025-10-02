/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.alicesworld.ModernConnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class Midlet extends MIDlet
        implements CommandListener {

    // display manager
    Display display;

    // a menu with items
    // main menu
    Form menu;

    // list of choices
    List choose;

    // textbox
    TextBox input;

    // ticker
    Ticker ticker = new Ticker(
            "Welcome to J2ME WebSocket");

    // alerts
    final Alert soundAlert
            = new Alert("sound Alert");

    SocketConnection socket;
    // form
    Form form = new Form("WebSocket Client");

    // gauge
    Gauge gauge = new Gauge("Progress Bar", false, 20, 9);

    // text field
    TextField textfield = new TextField(
            "TextField Label", "abc", 50, 0);

// command
    static final Command backCommand
            = new Command("Back", Command.BACK, 0);
    static final Command sendCommand
            = new Command("Send", Command.OK, 0);
    static final Command mainMenuCommand
            = new Command("Main", Command.SCREEN, 1);
    static final Command exitCommand
            = new Command("Exit", Command.STOP, 2);
    String currentMenu;
    TextField wsurl = new TextField("Websocket URL (host:port)", "wss://gateway.discord.gg/", 50, 0);

    // constructor.
    public Midlet() {
    }

    /**
     * Start the MIDlet by creating a list of items and associating the exit
     * command with it.
     */
    public void startApp() throws
            MIDletStateChangeException {
        display = Display.getDisplay(this);
        menu = new Form("Form for Stuff");
//      menu.append("Test Form", null);

        menu.append(wsurl);
        Command startCommand
                = new Command("Start", Command.OK, 0);
        menu.addCommand(startCommand);
        menu.addCommand(exitCommand);
        menu.setCommandListener(this);
        menu.setTicker(ticker);
        mainMenu();
        form.append(textfield);
        // today
    }

    public void pauseApp() {
        display = null;
        choose = null;
        menu = null;
        ticker = null;
        form = null;
        input = null;
        gauge = null;
        textfield = null;
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    // main menu
    void mainMenu() {
        display.setCurrent(menu);
        currentMenu = "Main";
//      (new SecureRandom()).nextInt();
    }
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
    WebSocketClient ws;

    /**
     * Test the Form component.
     */
    public void testForm() {

        String reply = "";
        try {
//            ws = (WebSocketClient) ModernConnector.open("wss://gateway.discord.gg/");
//            
//            reply = ws.receiveMessageString();
            textfield.setLabel("hey");
            HttpConnection hc = (HttpConnection) ModernConnector.open("https://cloudflare.com/cdn-cgi/trace");
            hc.setRequestMethod(HttpConnection.GET);
            hc.setRequestProperty("Accept", "*/*");

            // 4) Get code (forces the send), then read body
            int code = hc.getResponseCode();
            if (code < 200 || code >= 300) {
//                throw new IOException("HTTP " + code + " " + hc.getResponseMessage());
            }

            InputStream in = hc.openInputStream();
            String output = new String(readAll(in, Integer.MAX_VALUE));
            System.out.println(output);
            textfield.setLabel(output);
        } catch (IOException e) {
            e.printStackTrace();
            textfield.setLabel("** ERROR **\r\n" + e.getMessage());
            return;
        }
        
//        Runnable myRunnable;
//        myRunnable = new Runnable() {
//            public void run() {
//                while (true) {
//                    try {
//                        String message = ws.receiveMessageString();
//                        textfield.setLabel(textfield.getLabel() + "\r\n" + message);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                        textfield.setLabel(textfield.getLabel() + "\r\n" + "Session terminated");
//                        break;
//                    }
//
//                }
//            }
//        };

//        Thread thread = new Thread(myRunnable);
//        thread.start();
        form.addCommand(sendCommand);
        form.addCommand(backCommand);
        form.setCommandListener(this);
        display.setCurrent(form);
        currentMenu = "form";
    }

    /**
     * Handle events.
     */
    public void commandAction(Command c,
            Displayable d) {
        String label = c.getLabel();
        if (label.equals("Exit")) {
            destroyApp(true);
        } else if (label.equals("Back")) {
            if (currentMenu.equals("list")
                    || currentMenu.equals("input")
                    || currentMenu.equals("date")
                    || currentMenu.equals("form")) {
                // go back to menu
                mainMenu();
            }

        } else if (label.equals("Send")) {
            try {
                ws.sendMessage(textfield.getString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (label.equals("Start")) {
            testForm();

        } else {
            List down = (List) display.getCurrent();
            switch (down.getSelectedIndex()) {
                case 0:
                    testForm();
                    break;
            }

        }
    }
}
