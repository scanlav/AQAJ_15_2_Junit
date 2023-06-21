package exercise.article;

import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Тесты работника")
class WorkerImplTest {
    private WorkerImpl worker;

    @Mock
    private Library mockLibrary;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        worker = new WorkerImpl(mockLibrary);
    }

    @Test
    @DisplayName("Список доступных статей")
    public void testGetCatalog() {
        when(mockLibrary.getAllTitles()).thenReturn(Arrays.asList(
                "Title 1",
                "Title 2",
                "Title 3"
        ));

        String catalog = worker.getCatalog();

        String expectedCatalog = """
                Список доступных статей:
                    Title 1
                    Title 2
                    Title 3
                """;
        assertEquals(expectedCatalog, catalog);
    }

    @Test
    @DisplayName("Сохранение статьи без указания даты")
    public void testPrepareArticlesWithoutDate() {
        List<Article> articles = new ArrayList<>();

        articles.add(new Article("title", "content", "author",
                null));

        List<Article> preparedArticles = worker.prepareArticles(articles);

        List<Article> expectedArticles = new ArrayList<>();

        expectedArticles.add(new Article("title", "content", "author",
                LocalDate.now()));

        assertEquals(expectedArticles, preparedArticles);
    }

    @Test
    @DisplayName("Запрет сохранения статьи без указания заголовка")
    public void testPrepareArticlesWithoutTitle() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("", "content", "author",
                LocalDate.now()));

        List<Article> preparedArticles = worker.prepareArticles(articles);

        List<Article> expectedArticles = new ArrayList<>();

        assertEquals(expectedArticles, preparedArticles);
    }

    @Test
    @DisplayName("Запрет сохранения статьи без указания контента")
    public void testPrepareArticlesWithoutContent() {
        List<Article> articles = new ArrayList<>();

        articles.add(new Article("title", "", "author",
                LocalDate.now()));

        List<Article> preparedArticles = worker.prepareArticles(articles);

        List<Article> expectedArticles = new ArrayList<>();

        assertEquals(expectedArticles, preparedArticles);
    }

    @Test
    @DisplayName("Запрет сохранения статьи без указания автора")
    public void testPrepareArticlesWithoutAuthor() {
        List<Article> articles = new ArrayList<>();

        articles.add(new Article("title", "content", "",
                LocalDate.now()));

        List<Article> preparedArticles = worker.prepareArticles(articles);

        List<Article> expectedArticles = new ArrayList<>();

        assertEquals(expectedArticles, preparedArticles);
    }

    @Test
    @DisplayName("Сохранение правильно заполненной статьи")
    public void testPrepareArticles() {
        List<Article> articles = new ArrayList<>();

        articles.add(new Article("title", "content", "author",
                LocalDate.now()));

        List<Article> preparedArticles = worker.prepareArticles(articles);

        List<Article> expectedArticles = new ArrayList<>();
        expectedArticles.add(new Article("title", "content", "author",
                LocalDate.now()));

        assertEquals(expectedArticles, preparedArticles);
    }

    @Test
    @DisplayName("Запрет сохранения дублирующих статей") //бонус
    public void testAddNewArticlesSameContent() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("title", "content", "author",
                LocalDate.now()));
        articles.add(new Article("title", "content", "author 2 ",
                LocalDate.now()));
        articles.add(new Article("title 3", "content 3", "author 3",
                LocalDate.now()));

        assertEquals(2, worker.prepareArticles(articles).size());
    }

    @Test
    @DisplayName("Добавление новых статей") //проверка происходит через регистрацию вызова методов store()
    // и updateCatalog()
    public void testAddNewArticles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("title", "content", "author",
                LocalDate.now()));
        articles.add(new Article("title 2", "content 2", "author 2",
                LocalDate.now()));
        articles.add(new Article("title 3", "content 3", "author 3",
                LocalDate.now()));

        worker.addNewArticles(articles);

        verify(mockLibrary, atLeastOnce()).store(2023, articles);
        verify(mockLibrary, atLeastOnce()).updateCatalog();
    }
}



