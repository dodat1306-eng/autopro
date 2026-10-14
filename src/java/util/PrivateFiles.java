package util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public final class PrivateFiles {
    private PrivateFiles() {}
    private static Path root() {
        String dir=System.getProperty("AUTOPRO_PRIVATE_UPLOAD_DIR",System.getenv("AUTOPRO_PRIVATE_UPLOAD_DIR"));
        return (dir==null || dir.isBlank()?Paths.get(System.getProperty("user.home"),".autopro","private-documents"):Paths.get(dir)).toAbsolutePath().normalize();
    }
    public static String save(Part part) throws IOException {
        if(part==null || part.getSize()==0) return null;
        if(part.getSize()>5*1024*1024) throw new IllegalArgumentException("Ảnh tối đa 5 MB.");
        // Decode and re-encode: never trust the submitted extension or MIME type.
        try(InputStream in=part.getInputStream(); javax.imageio.stream.ImageInputStream input=ImageIO.createImageInputStream(in)) {
            java.util.Iterator<javax.imageio.ImageReader> readers=ImageIO.getImageReaders(input);
            if(!readers.hasNext()) throw new IllegalArgumentException("Vui lòng chọn ảnh PNG hoặc JPEG hợp lệ.");
            javax.imageio.ImageReader reader=readers.next();
            try {
                reader.setInput(input);
                long pixels=(long)reader.getWidth(0)*reader.getHeight(0);
                if(pixels>25000000 || pixels<1) throw new IllegalArgumentException("Kích thước ảnh không hợp lệ (tối đa 25 megapixel).");
                BufferedImage image=reader.read(0);
                Files.createDirectories(root());
                String key=UUID.randomUUID()+".png";
                ImageIO.write(image,"png",root().resolve(key).toFile());
                return "private/"+key;
            } finally { reader.dispose(); }
        }
    }
    public static Path resolve(String key, ServletContext context) throws IOException {
        if(key==null) throw new FileNotFoundException();
        Path base;
        String relative;
        if(key.startsWith("private/")) { base=root(); relative=key.substring(8); }
        else if(key.startsWith("uploads/seller-verify/")) {
            base=Paths.get(context.getRealPath("/uploads/seller-verify")).toAbsolutePath().normalize();
            relative=key.substring("uploads/seller-verify/".length());
        } else throw new FileNotFoundException();
        Path path=base.resolve(relative).normalize();
        if(!path.startsWith(base) || !Files.isRegularFile(path) || !path.toRealPath().startsWith(base.toRealPath())) throw new FileNotFoundException();
        return path;
    }
    public static void removeNew(String key) throws IOException {
        if(key!=null && key.matches("private/[a-f0-9-]{36}\\.png")) Files.deleteIfExists(root().resolve(key.substring(8)));
    }
}
