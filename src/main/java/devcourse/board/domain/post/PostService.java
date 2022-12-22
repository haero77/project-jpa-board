package devcourse.board.domain.post;

import devcourse.board.domain.member.MemberService;
import devcourse.board.domain.member.model.Member;
import devcourse.board.domain.post.model.Post;
import devcourse.board.domain.post.model.PostRequest;
import devcourse.board.domain.post.model.PostResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final MemberService memberService;

    private final PostRepository postRepository;

    public PostService(MemberService memberService, PostRepository postRepository) {
        this.memberService = memberService;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long createPost(PostRequest.CreationDto creationDto) {
        Member member = memberService.findOne(creationDto.getMemberId());

        Post post = PostRequest.toEntity(creationDto);
        post.createPost(member);

        postRepository.save(post);

        return post.getId();
    }

    public PostResponse findOneAsDto(Long postId) {
        return new PostResponse(findOne(postId));
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll()
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    public List<PostResponse> findWithPaging(int startPosition, int maxResultCount) {
        return postRepository.findWithPaging(startPosition, maxResultCount)
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional
    public Long updatePost(PostRequest.UpdateDto updateDto) {
        Post findPost = findOne(updateDto.getPostId());

        findPost.updateContents(updateDto.getTitle(), updateDto.getContent());

        return findPost.getId();
    }

    private Post findOne(Long postId) {
        return postRepository.findOne(postId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(
                        "Post doesn't exist for postId={0}", postId
                )));
    }
}
