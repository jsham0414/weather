package com.example.weather.repository;

import com.example.weather.domain.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {
    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = Memo.builder()
                .text("This is test memo")
                .build();

        // when
        jpaMemoRepository.save(newMemo);

        // then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertFalse(memoList.isEmpty());
    }

    @Test
    void findByIdTest() {
        // given
        Memo newMemo = Memo.builder()
                .text("This is test memo")
                .build();

        // when
        jpaMemoRepository.save(newMemo);

        // then
        Optional<Memo> result = jpaMemoRepository.findById(newMemo.getId());
        assertTrue(result.isPresent());
        assertEquals(result.get().getText(), "This is test memo");
    }

}