package UI;

import javafx.scene.media.*;
import java.io.*;
import java.net.*;

/**
 * Created by alex on 29/2/2016.
 */
public class Speech {
    /* field */
    private static final String DIRECTORY="audio/";
    private static boolean isPlaying=false;
    public enum LANGUAGE{ENGLISH,CANTONESE}


    /* constructor */
    //Singleton
    private Speech(){};



    /* methods */
    public static void play(String text,LANGUAGE language) throws IOException {
        if(!isPlaying){
            final String PATH=DIRECTORY+text+".mp3";
            File audio=new File(PATH);
            // check if the file exists
            if(!audio.exists()){
                //get from Google's server and save it under the designated directory
                if(!getAudioFromServer(text,language)) throw new IOException("Cannot get audio from Google");
            }
            // play the audio file, MP3 format as default
            isPlaying=true;
            AudioClip clip=new AudioClip(new File(PATH).toURI().toString());
            clip.play();
            isPlaying=false;
        }
    }
    public static boolean isPlaying(){return isPlaying;}


    private static URL getURL(String text,LANGUAGE language) throws MalformedURLException {
        String type=(language==LANGUAGE.CANTONESE)?"yue":"en";
        return new URL("http://translate.google.com/translate_tts?ie=UTF-8&q="+text+"&tl="+type+"&client=tw-ob");
    }
    private static boolean getAudioFromServer(String text,LANGUAGE language) throws IOException {
        URL url = getURL(text,language);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (con.getResponseCode() != 200) {
            System.err.println("Error from Google: HTTP" + con.getResponseCode() + "\t" + con.getResponseMessage());
            return false;
        }
        //saving as MP3
        InputStream in = con.getInputStream();
        FileOutputStream out = new FileOutputStream(text + ".mp3");
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return true;
    }
}
