package exercise.controller;

import exercise.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

import exercise.model.Post;
import exercise.repository.PostRepository;
import exercise.exception.ResourceNotFoundException;
import exercise.dto.PostDTO;
import exercise.dto.CommentDTO;

// BEGIN
import exercise.model.Comment;

@RestController
@RequestMapping("/posts")
public class PostsController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping()
    public List<PostDTO> index() {
        var postList = postRepository.findAll();
        var postDTOList = new ArrayList<PostDTO>();
        for(Post post : postList){
            var commentDTOList = commentRepository.findByPostId(post.getId())
                    .stream()
                    .map(this::toCommentDTO).toList();
            var postDTO = toPostDTO(post, commentDTOList);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    @GetMapping(path = "/{id}")
    public PostDTO show(@PathVariable long id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));

        var commentDTOList = commentRepository.findByPostId(id)
                .stream()
                .map(this::toCommentDTO).toList();
        var postDTO = toPostDTO(post, commentDTOList);

        return postDTO;
    }

    private CommentDTO toCommentDTO(Comment comment) {
        var commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setBody(comment.getBody());
        return commentDTO;
    }

    private PostDTO toPostDTO(Post post, List<CommentDTO> commentDTOList) {
        var postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setBody(post.getBody());
        postDTO.setComments(commentDTOList);
        return postDTO;
    }
}
// END
