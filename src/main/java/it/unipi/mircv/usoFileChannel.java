package it.unipi.mircv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class usoFileChannel {

    public static void main(String[] args) {

        String file = "C:\\Users\\max99\\Desktop\\uni\\2_MIRCV\\MIRCV_proj2\\data\\collection.tsv";
        Path path = Path.of(file);
        int numOfDocs = 0;
        int docLength = 0;


        try(FileChannel fchannel = FileChannel.open(path, StandardOpenOption.READ);) {

            ByteBuffer buffer = ByteBuffer.allocate(1024); // Buffer per i dati letti
            char t;

            while (fchannel.read(buffer) != -1) {
                buffer.flip(); // Passa dalla modalità di lettura
                while (buffer.hasRemaining()) {
                    t = (char) buffer.get();
                    /////System.out.print(t);
                    //if (t == ' ') docLength++;
                    //if (t == '\n'){
                    //    numOfDocs++;
                    //salvare docLength
                    //    docIndex
                    //   docLength = 0;
                    // }
                }
                buffer.clear(); // Pulisce il buffer per la lettura successiva
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}