package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post update(Long id, Post post) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        postRepository.delete(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        return post;
    }


    @Override
    public Post createPost(PostRequestDTO postRequestDTO, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        //list rỗng để lưu đường dẫn (String) của các ảnh sau khi upload
        List<String> mediaPaths = new ArrayList<>();

        //kiểm tra xem người dùng có gửi ảnh lên không
        if (postRequestDTO.getMediaUrls() != null && !postRequestDTO.getMediaUrls().isEmpty()) {
            try {
                //tạo đối tượng Path trỏ tới thư mục "uploads".
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    //nếu thư mục "uploads" chưa tồn tại → tạo mới
                    Files.createDirectories(uploadDir);
                }

                //duyệt qua từng file
                for (MultipartFile file : postRequestDTO.getMediaUrls()) {
                    //check nếu không tải lên file nào
                    if (file != null && !file.isEmpty()){
                        //lấy tên file string (tạo chuối radom + tên file gốc từ client)
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        //resolve nối (an toàn) tên file vào đường dẫn thư mục uploads (uploads/0a12b-uuid_anh1.png)
                        Path filePath = uploadDir.resolve(fileName);
                        //đọc dữ liệu từ file upload (getInputStream()) và ghi vào đường dẫn filePath
                        Files.copy(file.getInputStream(), filePath);
                        //thêm tên file vào list
                        mediaPaths.add("/uploads/" + fileName);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot save files: " + e.getMessage());
            }
        }

        //gộp tất cả đường dẫn trong mediaPaths thành một chuỗi duy nhất, cách nhau bằng dấu phẩy
        //chuyển list thành string (join) vì trong entity lưu string
        //chuyển string thành list (split)
        String mediaPathString = String.join(",", mediaPaths);

        Post post = postMapper.toPost(postRequestDTO, user, mediaPathString);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, PostRequestDTO postRequestDTO, String username) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        if(!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized user");
        }

        List<String> mediaPaths = new ArrayList<>();
        if(postRequestDTO.getMediaUrls() != null && !postRequestDTO.getMediaUrls().isEmpty()){
            try {
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                for(MultipartFile file : postRequestDTO.getMediaUrls()){
                    if(file != null && !file.isEmpty()){
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path filePath = uploadDir.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);
                        mediaPaths.add("/uploads/" + fileName);
                    }
                }
            }catch (Exception e){
                throw new RuntimeException("Cannot save files: " + e.getMessage());
            }
        }
        if(post.getMediaUrls() != null && !post.getMediaUrls().isEmpty()){
            String[] existingFile = post.getMediaUrls().split(","); //chuỗi -> mảng
            mediaPaths.addAll(Arrays.asList(existingFile));
        }
        String mediaPathString = String.join(",", mediaPaths); //mảng -> chuỗi
        post.setContent(postRequestDTO.getContent());
        post.setMediaUrls(mediaPathString);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
}
