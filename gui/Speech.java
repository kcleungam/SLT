package gui;

import javafx.scene.media.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 29/2/2016.
 */
public class Speech {
    /* field */
    private static final String DIRECTORY="audio\\";
    private static boolean isPlaying=false;
    public enum LANGUAGE{ENGLISH,CANTONESE}
    public static final String ENGLISH_REGEX="\\p{Print}";
    public static final String CANTONESE_REGEX="\\p{InCJKUnifiedIdeographs}";


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

    public static void play(String text) throws IOException {
        Pattern pattern=Pattern.compile(CANTONESE_REGEX);//
        Matcher matcher=pattern.matcher(text);
        //if it contains Han script, then use Cantonese
        if(matcher.find())
            play(text,LANGUAGE.CANTONESE);
        else
            play(text,LANGUAGE.ENGLISH);
    }

    public static boolean isPlaying(){return isPlaying;}


    private static URL getURL(String text,LANGUAGE language) throws MalformedURLException {
        String type=(language==LANGUAGE.CANTONESE)?"yue":"en";
        return new URL("http://translate.google.com/translate_tts?ie=UTF-8&q="+(text.replace(" ","%20"))+"&tl="+type+"&client=tw-ob");
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
        FileOutputStream out = new FileOutputStream("audio\\" + text + ".mp3");
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return true;
    }

    /**
     * Just see whether the given gesture name contains printable ASCII or CJK unified ideographs
     * @param name
     * @return
     */
    public static boolean validate(String name){
        Pattern pattern=Pattern.compile("[+"+ENGLISH_REGEX+"["+CANTONESE_REGEX+"]"+"]");
        Matcher matcher=pattern.matcher(name);
        return matcher.find();
    }
}
