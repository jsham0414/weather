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
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = Memo.builder()
                .id(1)
                .text("this is a new memo")
                .build();

        // when
        jdbcMemoRepository.save(newMemo);
        Optional<Memo> finded = jdbcMemoRepository.findById(1);

        // then
        assertNotNull(finded);
        assertEquals(newMemo.getId(), finded.get().getId());
    }

    @Test
    void findAllMemoTest() {
        // given
        Memo firstMemo = Memo.builder()
                .id(1)
                .text("this is a first memo")
                .build();

        Memo secondMemo = Memo.builder()
                .id(2)
                .text("this is a second memo")
                .build();

        // when
        jdbcMemoRepository.save(firstMemo);
        jdbcMemoRepository.save(secondMemo);
        List<Memo> memoList = jdbcMemoRepository.findAll();

        // then
        memoList.forEach(m -> {
            System.out.println(m.toString());
        });
        assertNotNull(memoList);
    }
}