/**
 *
 */
package com.example.sstto.sos;

import android.os.Message;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Connect {

    private MainActivity mainActivity;
    public Document doc=null;

    public Connect(MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;
    }

    /**
     * Web request and parse response xml
     */
    protected void request() {
        URL url = null;
        try
        {
            /*Server URL appended with location coordinates*/
            url = new URL("http:/192.168.0.8:8081?long="+mainActivity.longitude+"&lat="+mainActivity.latitude);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try
        {
            /*Connection to the URL*/
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(2500);
        }
        /*Exception in connection*/
        catch (IOException e)
        {
            //Ain't Nobody Got Time for That Server
            /*Set connection error to display in UI*/
            mainActivity.status=-1;
            mainActivity.alert="UNABLE TO CONNECT";
            mainActivity.tip="Its the end of the world or\n...our server is in SOS.\nDon't worry, Time heals everything\nWill be back up shortly";
            mainActivity.safeZone=" ";
            /*Post Interrupt to UI thread*/
            e.printStackTrace();
            Message message1 = mainActivity.handler.obtainMessage(0, "Oops couldn't connect, You are on your own");
            message1.sendToTarget();
            return;
        }
        try
        {
            /*Read response from Web request*/
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int ch=in.read();
            while(ch != -1)
            {
                out.write(ch);
                ch = in.read();
            }
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(out.toString())));
            /*Parse response XML*/
            mainActivity.status=Integer.parseInt(doc.getElementsByTagName("STATUS").item(0).getChildNodes().item(0).getNodeValue());
            mainActivity.alert=doc.getElementsByTagName("ALERT").item(0).getChildNodes().item(0).getNodeValue();
            mainActivity.tip=doc.getElementsByTagName("TIP").item(0).getChildNodes().item(0).getNodeValue();
            mainActivity.safeZone=doc.getElementsByTagName("ADDRESS").item(0).getChildNodes().item(0).getNodeValue();
        }
        catch (IOException|SAXException|ParserConfigurationException   e)
        {

            /*Set connection error to display in UI*/
            mainActivity.status=-1;
            mainActivity.alert="UNABLE TO CONNECT";
            mainActivity.tip="Server is in SOS.\nIt will be back up shortly";
            mainActivity.safeZone=" ";
            /*Post Interrupt to UI thread*/
            e.printStackTrace();
            Message message1 = mainActivity.handler.obtainMessage(0, "Oops couldn't connect, You are on your own");
            message1.sendToTarget();
            return;
        }
        finally
        {
            urlConnection.disconnect();
        }
    }
}
