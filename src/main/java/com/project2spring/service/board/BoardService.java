package com.project2spring.service.board;

import com.project2spring.domain.board.Board;
import com.project2spring.domain.board.BoardFile;
import com.project2spring.mapper.board.BoardMapper;
import com.project2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public void add(Board board, MultipartFile[] files, Authentication authentication) throws IOException {
        board.setMemberId(Integer.valueOf(authentication.getName()));
        mapper.insert(board); //게시물 저장 먼저

        if (files != null) {
            for (MultipartFile file : files) {
                //DB에 해당 게시물의 파일 목록 저장
                mapper.insertFileName(board.getId(), file.getOriginalFilename());

                // 실제 파일 저장
                // 부모 디렉토리 만들기
                String dir = STR."C:/Temp/prj2/\{board.getId()}";
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                //파일 경로
                String path = STR."C:/Temp/prj2/\{board.getId()}/\{file.getOriginalFilename()}";
                File destination = new File(path);
                file.transferTo(destination);
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
                .map(name -> new BoardFile(name, STR."http://172.30.1.56:8888/\{id}/\{name}"))
                .toList();
        board.setFileList(files);

        // http://172.30.1.56:8888/{id}/{name}
        return board;
    }

    public void remove(Integer id) {

        // file명 조회
        List<String> fileNames = mapper.selectFileNameByBoardId(id);

        // disk에 있는 file 삭제
        String dir = STR."C:/Temp/prj2/\{id}/";
        for (String fileName : fileNames) {
            File file = new File(dir + fileName);
            file.delete();
        }
        File dirFile = new File(dir);
        if (dirFile.exists()) {
            dirFile.delete();
        }

        // board_file 삭제
        mapper.deleteFileByBoardId(id);

        // board
        mapper.deleteById(id);
    }

    public void edit(Board board, List<String> removeFileList) {
        if (removeFileList != null && removeFileList.size() > 0) {

            // disk의 파일 삭제
            for (String fileName : removeFileList) {
                String path = STR."C:/Temp/prj2/\{board.getId()}/\{fileName}";
                File file = new File(path);
                file.delete();

                // db records 삭제
                mapper.deleteFileByBoardIdAndName(board.getId(), fileName);
            }
        }


        mapper.update(board);
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        Board board = mapper.selectById(id); //게시물 번호
        return board.getMemberId().equals(Integer.valueOf(authentication.getName()));
    }
}
