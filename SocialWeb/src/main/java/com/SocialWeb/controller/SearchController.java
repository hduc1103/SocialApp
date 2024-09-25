package com.SocialWeb.controller;

import com.SocialWeb.entity.User;
import com.SocialWeb.entity.Post;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/result")
    public Map<String, Object> searchCombined(@RequestParam("keyword") String keyword) {
        List<User> users = userRepository.searchUsersByUsername(keyword);
        List<Post> posts = postRepository.searchPostsByContent(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("posts", posts);

        return result;
    }
}
