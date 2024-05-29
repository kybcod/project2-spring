package com.project2spring.service.board;

import com.project2spring.domain.board.Board;
import com.project2spring.domain.board.BoardFile;
import com.project2spring.mapper.board.BoardMapper;
import com.project2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;
    private final MemberMapper memberMapper;
    final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    String bucketName;

    @Value("${image.src.prefix}")
    String srcPrefix;

    public void add(Board board, MultipartFile[] files, Authentication authentication) throws IOException {
        board.setMemberId(Integer.valueOf(authentication.getName()));
        // 게시물 저장
        mapper.insert(board);

        if (files != null) {
            for (MultipartFile file : files) {
                // db에 해당 게시물의 파일 목록 저장
                mapper.insertFileName(board.getId(), file.getOriginalFilename());
                // 실제 파일 저장 (s3)
                String key = STR."prj2/\{board.getId()}/\{file.getOriginalFilename()}";
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();

                s3Client.putObject(objectRequest,
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            }
        }
    }

    // 제목, 내용, 작성자 유효성 검사(작성되었는지)
    public boolean validate(Board board) {

        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }

        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String searchType, String keyword) {

        // 페이지 번호
        Map pageInfo = new HashMap();
        Integer countAll = mapper.countAllWithSearch(searchType, keyword);

        Integer offset = (page - 1) * 10;
        Integer lastPageNumber = (countAll - 1) / 10 + 1;
        Integer leftPageNumber = (page - 1) / 10 * 10 + 1; // 현재 페이지 기준 왼쪽
        Integer rightPageNumber = leftPageNumber + 9; // 현재 페이지 기준 오른쪽
        Integer nextPageNumber = rightPageNumber + 1; // 다음
        Integer prevPageNumber = leftPageNumber - 1; // 이전
        rightPageNumber = Math.min(rightPageNumber, lastPageNumber);
        leftPageNumber = rightPageNumber - 9;
        leftPageNumber = Math.max(leftPageNumber, 1);

        // lastPageNumber는 10씩 끊어주기 때문에 전체 페이지 수보다 클 수 있다.

        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }

        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        pageInfo.put("currentPageNumber", page);
        pageInfo.put("lastPageNumber", lastPageNumber);
        pageInfo.put("leftPageNumber", leftPageNumber);
        pageInfo.put("rightPageNumber", rightPageNumber);

        // 검색어

        return Map.of("pageInfo", pageInfo,
                "boardList", mapper.selectAllPaging(offset, searchType, keyword));
    }


    // 게시물 하나 조회
    public Board get(Integer id) {
        Board board = mapper.selectById(id);
        List<String> fileNames = mapper.selectFileNameByBoardId(id);
        List<BoardFile> files = fileNames.stream()
                .map(name -> new BoardFile(name, STR."\{srcPrefix}\{id}/\{name}"))
                .toList();
        board.setFileList(files);
        return board;
    }

    public void remove(Integer id) {

        // file명 조회
        List<String> fileNames = mapper.selectFileNameByBoardId(id);

        // s3에 있는 file 삭제
        for (String fileName : fileNames) {
            String key = STR."prj2/\{id}/\{fileName}";
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(objectRequest);
        }

        // board_file 삭제
        mapper.deleteFileByBoardId(id);

        // board
        mapper.deleteById(id);
    }

    public void edit(Board board, List<String> removeFileList, MultipartFile[] addFileList) throws IOException {
        if (removeFileList != null && removeFileList.size() > 0) {

            // s3의 파일 삭제
            for (String fileName : removeFileList) {
                String key = STR."prj2/\{board.getId()}/\{fileName}";
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                s3Client.deleteObject(objectRequest);

                // db records 삭제
                mapper.deleteFileByBoardIdAndName(board.getId(), fileName);
            }
        }

        if (addFileList != null && addFileList.length > 0) {
            List<String> fileNameList = mapper.selectFileNameByBoardId(board.getId());
            for (MultipartFile file : addFileList) {
                String fileName = file.getOriginalFilename();
                if (!fileNameList.contains(fileName)) {
                    // 새 파일이 없을 때만 새 파일 DB에 추가
                    mapper.insertFileName(board.getId(), fileName);
                }

                // s3에 쓰기
                String key = STR."prj2/\{board.getId()}/\{fileName}";
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            }
        }


        mapper.update(board);
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        Board board = mapper.selectById(id); //게시물 번호
        return board.getMemberId().equals(Integer.valueOf(authentication.getName()));
    }

    public void like(Map<String, Object> req, Authentication authentication) {
        Integer boardId = (Integer) req.get("boardId");
        Integer memberId = Integer.valueOf(authentication.getName());

        // 이미 좋아요가 되어 있다면 delete(count=1)
        int count = mapper.deleteLikeByBoardIdAndMemberId(boardId, memberId);

        // 좋아요 안했으면 insert
        if (count == 0) {
            mapper.insertLikeByBoardIdAndMemberId(boardId, memberId);
        }
    }
}
