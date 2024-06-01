package com.project2spring.service.member;

import com.project2spring.domain.board.Board;
import com.project2spring.domain.member.Member;
import com.project2spring.mapper.board.BoardMapper;
import com.project2spring.mapper.comment.CommentMapper;
import com.project2spring.mapper.member.MemberMapper;
import com.project2spring.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final BoardMapper boardMapper;
    private final BoardService boardService;
    private final CommentMapper commentMapper;
    final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    String bucketName;

    @Value("${image.src.prefix.profile}")
    String srcPrefix;

    public void add(Member member, MultipartFile file) throws IOException {

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setEmail(member.getEmail().trim());
        member.setNickName(member.getNickName().trim());
        mapper.insert(member);

        if (file != null) {
//            String key = STR."prj/default/\{file.getOriginalFilename()}";
            String key = STR."prj/\{member.getId()}/\{file.getOriginalFilename()}";
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(objectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            member.setProfile(file.getOriginalFilename());
            mapper.updateProfile(member);
        }


    }

    public Member getByEmail(String email) {
        return mapper.selectByEmail(email.trim());
    }

    public Member getByNickName(String nickName) {
        return mapper.selectByNickName(nickName.trim());
    }

    public boolean validate(Member member) {
        if (member.getNickName() == null || member.getNickName().isBlank()) {
            return false;
        }
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return false;
        }
        if (member.getPassword() == null || member.getPassword().isBlank()) {
            return false;
        }
        // 이메일 형식
        String emailPattern = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*";
        if (!member.getEmail().trim().matches(emailPattern)) {
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String type, String keyword) {
        Map<String, Object> memberPageInfo = new HashMap<>();
        Integer offset = (page - 1) * 10;
        Integer countAll = mapper.countAll(type, keyword);
        Integer lastPage = (countAll - 1) / 10 + 1;
        Integer beginPage = (page - 1) / 5 * 5 + 1;
        Integer endPage = beginPage + 4;
        endPage = Math.min(endPage, lastPage);
        Integer nextPage = beginPage + 5;
        Integer prevPage = beginPage - 5;

        if (nextPage <= lastPage) {
            memberPageInfo.put("nextPage", nextPage);
        }

        if (prevPage > 0) {
            memberPageInfo.put("prevPage", prevPage);
        }

        memberPageInfo.put("currentPage", page);
        memberPageInfo.put("lastPage", lastPage);
        memberPageInfo.put("beginPage", beginPage);
        memberPageInfo.put("endPage", endPage);

        return Map.of("memberList", mapper.selectAllPaging(offset, type, keyword),
                "memberPageInfo", memberPageInfo);
    }

    public Member get(Integer id) {
        Member member = mapper.selectById(id);
        member.setAwsProfile(srcPrefix);
        return member;
    }

    public void delete(Integer id) throws IOException {

        // 회원이 쓴 게시물 조회
        List<Board> boardList = boardMapper.selectByMemberId(id);

        // 각 게시물 지우기
        boardList.forEach(board -> boardService.remove(board.getId()));

        // 좋아요 지우기
        boardMapper.deleteLikeByMemberId(id);

        // 댓글 지우기
        commentMapper.deleteByMemberId(id);

        Member member = mapper.selectById(id);
        String key = STR."prj/\{id}/\{member.getProfile()}";
        DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(objectRequest);

        // board 지운 후 member 테이블 지우기
        mapper.deleteById(id);
    }

    public boolean hasAccess(Member member, Authentication authentication) {
        if (!member.getId().toString().equals(authentication.getName())) {
            return false;
        }
        Member dbMember = mapper.selectById(member.getId());
        if (dbMember == null) {
            return false;
        }

        return passwordEncoder.matches(member.getPassword(), dbMember.getPassword());
    }

    public Map<String, Object> modify(Member member, MultipartFile file, Authentication authentication) throws IOException {
        if (member.getPassword() != null && member.getPassword().length() > 0) {
            // 패스워드가 입력(변경)되었으니 바꾸기
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        } else {
            // 패스워드가 입력이 안됐으면 기존 값으로 유지
            Member dbMember = mapper.selectById(member.getId());
            member.setPassword(dbMember.getPassword());
        }

        if (file != null && !file.getOriginalFilename().equals(member.getProfile())) {

            //삭제
            String key = STR."prj/\{member.getId()}/\{member.getProfile()}";
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(objectRequest);

            // 추가
            String newKey = STR."prj/\{member.getId()}/\{file.getOriginalFilename()}";
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newKey)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            member.setProfile(file.getOriginalFilename());
            mapper.updateProfile(member);
        } else {
            member.setProfile(null);
            mapper.updateProfile(member);
        }

        mapper.update(member);


        String token = "";

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        JwtClaimsSet.Builder jwtClaimsSetBuilder = JwtClaimsSet.builder();
        claims.forEach(jwtClaimsSetBuilder::claim);
        jwtClaimsSetBuilder.claim("nickName", member.getNickName());

        JwtClaimsSet jwtClaimsSet = jwtClaimsSetBuilder.build();
        token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        return Map.of("token", token);
    }

    public boolean hasAccessModify(Member member, Authentication authentication) {
        if (!authentication.getName().equals(member.getId().toString())) {
            return false;
        }
        Member dbMember = mapper.selectById(member.getId());
        if (dbMember == null) {
            return false;
        }

        if (!passwordEncoder.matches(member.getOldPassword(), dbMember.getPassword())) {
            return false;
        }

        return true;
    }

    public Map<String, Object> getToken(Member member) {

        Map<String, Object> result = null;

        Member db = mapper.selectByEmail(member.getEmail());

        if (db != null) {
            if (passwordEncoder.matches(member.getPassword(), db.getPassword())) {
                result = new HashMap<>();
                String token = "";
                List<String> authority = mapper.selectAuthorityByMemberId(db.getId());
                String authorityString = authority.stream().collect(Collectors.joining(" "));

                // 토큰 만드는 코드
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7)) //일주일
                        .subject(db.getId().toString())
                        .claim("scope", authorityString) //권한
                        .claim("nickName", db.getNickName())
                        .build();
                token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                result.put("token", token);
            }
        }
        return result;
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        boolean self = authentication.getName().equals(id.toString());
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SCOPE_admin"));
        return self || isAdmin;

    }
}
