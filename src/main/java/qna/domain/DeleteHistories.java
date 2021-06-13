package qna.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DeleteHistories {
    private final List<DeleteHistory> histories;

    public DeleteHistories(List<DeleteHistory> histories) {
        this.histories = histories;
    }

    public DeleteHistories() {
        this.histories = new ArrayList<>();
    }

    public void addDeleteHistory(DeleteHistory deleteHistory) {
        this.histories.add(deleteHistory);
    }

    public List<DeleteHistory> getHistories() {
        return (List<DeleteHistory>) Collections.unmodifiableCollection(histories);
    }

    public DeleteHistories concat(DeleteHistories other) {
        ArrayList<DeleteHistory> concatList = new ArrayList<>();
        concatList.addAll(other.histories);
        return new DeleteHistories(concatList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteHistories that = (DeleteHistories) o;
        return Objects.equals(histories, that.histories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(histories);
    }

    public static DeleteHistories deleteHistoriesCreate(Question question, Answers answers, User loginUser){
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        question.deletedByUser(loginUser);
        deleteHistoryCreate(question, loginUser);

        answers.deleteAnswers(loginUser);
        new DeleteHistory(ContentType.ANSWER, loginUser.getId(), question.getWriter(), LocalDateTime
            .now());

        return new DeleteHistories(deleteHistories);
    }

    private static void deleteHistoryCreate(Question question, User loginUser) {

        DeleteHistories deleteHistories = new DeleteHistories();
        deleteHistories.addDeleteHistory(new DeleteHistory(ContentType.QUESTION, question.getId(), question.getWriter(), LocalDateTime.now()));

        question.getAnswers().deleteAnswers(loginUser);
    }
}