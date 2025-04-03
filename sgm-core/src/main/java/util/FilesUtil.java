/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.origami.config.SisVars;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Angel Navarro
 */
public class FilesUtil implements Serializable {

    private static final Logger LOG = Logger.getLogger(FilesUtil.class.getName());

    /**
     * Copia el archivo en el servidor y de vualve el archivo guardado
     *
     * @param event FileUploadEvent de primefaces
     * @return File del archivo guardado en el servidor.
     * @throws java.io.IOException
     */
    public static File copyFileServer(FileUploadEvent event) throws IOException {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + d.getTime() + "-" + event.getFile().getFileName());

            InputStream is;
            is = event.getFile().getInputstream();
            try (OutputStream out = new FileOutputStream(file)) {
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            is.close();
            return file;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Copiar Archivo al servidor", e);
        }
        return null;
    }

    /**
     * Return Copia el archivo en el servidor y de vualve el archivo guardado
     *
     * @param event FileUploadEvent de primefaces
     * @return InputStream
     * @throws java.io.IOException
     */
    public static InputStream copyFileServer1(FileUploadEvent event) throws IOException {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + d.getTime() + "-" + event.getFile().getFileName());

            InputStream is;
            is = event.getFile().getInputstream();
            try (OutputStream out = new FileOutputStream(file)) {
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            is.close();
            return new FileInputStream(file);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Copiar Archivo al servidor", e);
        }
        return null;
    }

    /**
     * Copia los archivos en la ruta prederminata de archivos y los pone en
     * directorio que se recive como parametro.
     *
     * @param event FileUploadEvent de primefaces
     * @param directorio Directorio donde se guardaran las fotos.
     * @return
     * @throws IOException
     */
    public static File copyFileServer(FileUploadEvent event, String directorio) throws IOException {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + "/" + directorio + "/" + d.getTime() + "-" + event.getFile().getFileName());
            System.out.println("file" +file.length());
            InputStream is = event.getFile().getInputstream();
            try (OutputStream out = new FileOutputStream(file)) {
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            is.close();
            return file;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Copiar Archivo al servidor", e);
        }
        return null;
    }

    public static File copyFileServer(List<UploadedFile> files, String directorio) throws IOException {
        try {
            Path path = Paths.get(SisVars.rutaRepotiorioArchivo + "/" + directorio);
            Files.createDirectories(path);
            for (UploadedFile uFile : files) {
                File file = new File(SisVars.rutaRepotiorioArchivo + "/" + directorio + "/" + uFile.getFileName());
//                System.out.println(file.getName() + " >> " + file.length()); // tamanio de foto.
                try (InputStream is = uFile.getInputstream(); OutputStream out = new FileOutputStream(file)) {
                    byte buf[] = new byte[2048];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                return path.toFile();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Copiar Archivo al servidor", e);
        }
        return null;
    }

    public static File copyFileServer1(List<UploadedFile> files, String directorio) throws IOException {
        try {
            Path path = Paths.get(SisVars.rutaRepotiorioArchivo + "/" + directorio);
            Files.createDirectories(path);
            for (UploadedFile uFile : files) {
                File file = new File(SisVars.rutaRepotiorioArchivo + "/" + directorio + "/" + uFile.getFileName());
//                System.out.println(file.getName() + " >> " + file.length()); // tamanio de foto.
                try (InputStream is = uFile.getInputstream(); OutputStream out = new FileOutputStream(file)) {
                    byte buf[] = IOUtils.toByteArray(is);
                    if (buf.length > 0) {
                        out.write(buf);
                    }
                }
                return path.toFile();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Copiar Archivo al servidor", e);
        }
        return null;
    }

}
