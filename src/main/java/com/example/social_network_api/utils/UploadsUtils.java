package com.example.social_network_api.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UploadsUtils {
    public static String uploadFile(MultipartFile file) {
        String avatarPath = null;
        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("uploads").resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                avatarPath = "/uploads/" + fileName;
            }
            return avatarPath;
        } catch (Exception e) {
            throw new RuntimeException("Cannot save files: " + e.getMessage());
        }
    }

    public static List<String> uploadFiles(List<MultipartFile> files) {
        if(files == null || files.isEmpty()){
            return  null;
        }
        try {
            //tạo đối tượng Path trỏ tới thư mục "uploads".
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                //nếu thư mục "uploads" chưa tồn tại → tạo mới
                Files.createDirectories(uploadDir);
            }

            List<String> mediaPaths = new ArrayList<>();
            //duyệt qua từng file
            for (MultipartFile file : files) {
                //check nếu không tải lên file nào
                if (file != null && !file.isEmpty()){
                    //lấy tên file string (tạo chuối radom + tên file gốc từ client)
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    //resolve nối (an toàn) tên file vào đường dẫn thư mục uploads (uploads/0a12b-uuid_anh1.png)
                    Path filePath = uploadDir.resolve(fileName);
                    //đọc dữ liệu từ file upload (getInputStream()) và ghi vào đường dẫn filePath
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    //thêm tên file vào list
                    mediaPaths.add("/uploads/" + fileName);
                }
            }
            return mediaPaths;
        } catch (IOException e) {
            throw new RuntimeException("Cannot save files: " + e.getMessage());
        }
    }

    public static void deleteFile(String filePath) {
        try {
            if (filePath != null) {
                // bỏ dấu "/" ở đầu nếu có
                if (filePath.startsWith("/")) {
                    filePath = filePath.substring(1); //lấy từ index = 1 trở đi
                }
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete file: " + e.getMessage());
        }
    }

}
