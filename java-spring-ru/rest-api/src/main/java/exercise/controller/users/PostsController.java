package exercise.controller.users;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import exercise.model.Post;
import exercise.Data;

// BEGIN
@RestController
@RequestMapping("/api")
public class PostsController {
    private List<Post> posts = Data.getPosts();

    @GetMapping("/users/{id}/posts") // список всех постов, написанных пользователем с таким же userId, как id в маршруте
    public List<Post> index(@PathVariable String id) {
        return posts.stream().filter(p -> (String.valueOf(p.getUserId()).equals(id))).toList();
    }

    @PostMapping("/users/{id}/posts") // создание нового поста, привязанного к пользователю по id
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@PathVariable int id, @RequestBody Post post){
        Post newPost = post;
        newPost.setUserId(id);
        posts.add(newPost);
        return post;
    }
}
// END
