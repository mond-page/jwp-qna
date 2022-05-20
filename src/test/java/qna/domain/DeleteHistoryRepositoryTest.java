package qna.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static qna.domain.UserTest.MOND;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class DeleteHistoryRepositoryTest {
    private DeleteHistory deleteHistory;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    DeleteHistoryRepository deleteHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User mond = userRepository.findByUserId(MOND.getUserId())
                .orElseGet(() -> userRepository.save(MOND));

        deleteHistory = new DeleteHistory(new DeleteHistoryContent(ContentType.QUESTION, 1L), mond, LocalDateTime.now());
    }

    @Test
    @DisplayName("영속 상태의 동일성 보장 검증")
    void verifyEntityPrimaryCacheSave() {
        DeleteHistory expected = deleteHistoryRepository.save(deleteHistory);
        Optional<DeleteHistory> actual = deleteHistoryRepository.findById(expected.getId());

        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> verifyEqualDeleteHistoryFields(actual.get(), expected)
        );
    }

    @Test
    @DisplayName("준영속 상태의 동일성 보장 검증")
    void verifyEntityDatabaseSave() {
        DeleteHistory expected = deleteHistoryRepository.save(deleteHistory);
        entityFlushAndClear();
        Optional<DeleteHistory> actual = deleteHistoryRepository.findById(expected.getId());

        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> verifyEqualDeleteHistoryFields(actual.get(), expected)
        );
    }

    private void entityFlushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }

    private void verifyEqualDeleteHistoryFields(DeleteHistory dh1, DeleteHistory dh2) {
        assertAll(
                () -> assertThat(dh1.getId()).isEqualTo(dh2.getId()),
                () -> assertThat(dh1.contentInformation()).isEqualTo(dh2.contentInformation()),
                () -> assertThat(dh1.getDeleter()).isEqualTo(dh2.getDeleter()),
                () -> assertThat(dh1.getCreateDate()).isEqualTo(dh2.getCreateDate())
        );
    }
}
