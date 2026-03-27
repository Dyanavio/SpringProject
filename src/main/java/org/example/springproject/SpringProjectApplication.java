package org.example.springproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


@SpringBootApplication
public class SpringProjectApplication
{
    static void main(String[] args) {
        SpringApplication.run(SpringProjectApplication.class, args);
    }
}


@Component
class BrowserLauncher
{
    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser()
    {
        System.setProperty("java.awt.headless", "false");
        var desktop = Desktop.getDesktop();
        try
        {
            desktop.browse(new URI("http://localhost:8080"));
        }
        catch (IOException | URISyntaxException e) {}
    }
}


/*

// модель даних для діджея (бажано винести в окремий файл у реальному проєкті)
class DJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("name") // анотація для серіалізації в JSON
    @NotBlank(message = "ім'я не може бути порожнім") // анотація для валідації, буде потрібна при додаванні через API
    @Size(max = 100, message = "ім'я не може бути довшим за 100 символів")
    private String name;

    @JsonProperty("popularityRank")
    @Min(value = 1, message = "рейтинг повинен бути більшим за 0")
    private int popularityRank;

    public DJ() {} // конструктор за замовчуванням для серіалізації (вимога бібліотеки Jackson та рекомендація при створенні JavaBean)

    public DJ(String name, int popularityRank) {
        this.name = name;
        this.popularityRank = popularityRank;
    }

    // геттери та сеттери
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopularityRank() {
        return popularityRank;
    }

    public void setPopularityRank(int popularityRank) {
        this.popularityRank = popularityRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DJ dj = (DJ) o;
        return popularityRank == dj.popularityRank && name.equals(dj.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + popularityRank;
    }

    @Override
    public String toString() {
        return "DJ{" + "name='" + name + '\'' + ", popularityRank=" + popularityRank + '}';
    }
}

// сервіс для роботи з даними
// сервіс в спрінг фреймворк - це компонент бізнес-логіки.
// він автоматично реєструється як Spring Bean (завдяки component scanning), стає singleton'ом за замовчуванням
// і доступний для ін'єкції залежностей (наприклад, через анотацію @Autowired).
// сервіси зазвичай реалізують операції з даними (CRUD, валідція, сортування), відокремлюючи логіку
// від контролерів (REST/UI) та репозиторіїв (DAO).
@Service
class DJService {
    private final List<DJ> djs = new ArrayList<>();

    public DJService() {
        // початкові дані для прикладу
        djs.add(new DJ("Armin van Buuren", 1));
        djs.add(new DJ("Markus Schulz", 2));
        djs.add(new DJ("Above & Beyond", 3));
        djs.add(new DJ("Cosmic Gate", 4));
        djs.add(new DJ("Anna Lee", 5));
        djs.add(new DJ("Charlotte de Witte", 6));
        djs.add(new DJ("Nifra", 7));
        djs.add(new DJ("Joyce Muniz", 8));
        djs.add(new DJ("Sara Landry", 9));
        djs.add(new DJ("Amelie Lens", 10));
    }

    // отримати всіх діджеїв (відсортовано за рейтингом)
    public List<DJ> getAllDJs() {
        return djs.stream().sorted(Comparator.comparingInt(DJ::getPopularityRank)).collect(Collectors.toList());
    }

    // отримати діджея за id
    public DJ getDJById(int id) {
        if (id < 1 || id > djs.size()) {
            throw new NoSuchElementException("dj з id " + id + " не знайдено");
        }
        return djs.get(id - 1);
    }

    // додати нового діджея
    public void addDJ(DJ dj) {
        if (djs.stream().anyMatch(existing -> existing.getPopularityRank() == dj.getPopularityRank())) {
            throw new IllegalArgumentException("рейтинг " + dj.getPopularityRank() + " вже зайнятий");
        }
        if (dj.getPopularityRank() <= 0) {
            throw new IllegalArgumentException("рейтинг повинен бути більшим за 0");
        }
        djs.add(dj);
    }
}

// REST-контролер для API
// це спеціалізований контролер для REST API, що комбінує @Controller (обробка HTTP-запитів)
// та @ResponseBody (автоматична серіалізація відповідей у JSON/XML).
// він обробляє запити (GET/POST тощо) і повертає дані безпосередньо (не HTML-види!), інжектує
// залежності (DJService), і беспосередньо тут мапить /api/djs на методи для CRUD (getDJ, addDJ)
// @RestController = "Spring, обробляй API-запити та повертай JSON".
@RestController
@RequestMapping("/api/djs") // перевірити можна через http://localhost:8080/api/djs/1
class DJApiController {
    private final DJService djService;

    public DJApiController(DJService djService) {
        this.djService = djService;
    }

    // отримати діджея за id
    @GetMapping("/{id}")
    public ResponseEntity<DJ> getDJ(@PathVariable("id") int id) {
        DJ dj = djService.getDJById(id);
        return new ResponseEntity<>(dj, HttpStatus.OK);
    }

    // отримати список всіх діджеїв
    @GetMapping
    public ResponseEntity<List<DJ>> getAllDJs() {
        return new ResponseEntity<>(djService.getAllDJs(), HttpStatus.OK);
    }

    // додати діджея через API
    @PostMapping
    public ResponseEntity<DJ> addDJ(@RequestBody @Valid DJ dj) {
        djService.addDJ(dj);
        return new ResponseEntity<>(dj, HttpStatus.CREATED);
    }
}

// веб-контролер для html-сторінок
@Controller
@RequestMapping("/")
class WebController {
    private final DJService djService;

    public WebController(DJService djService) {
        this.djService = djService;
    }

    // головна сторінка
    @GetMapping // @GetMapping без параметра = мапінг на поточний шлях класу (@RequestMapping("/"))
    // метод повертає String - назву шаблону Thymeleaf (не ResponseEntity, бо тут уже MVC, не REST)
    public String index(Model model, RedirectAttributes redirectAttributes) {
        // Model - це стандартний Spring MVC об'єкт (автоматично створюється та інжектується в параметри методу),
        // використовується для передачі даних з контролера у view (Thymeleaf-шаблон).
        // addAttribute("ключ", значення) - додає атрибут, доступний у шаблоні як ${ключ} (EL-вирази Thymeleaf).
        model.addAttribute("djs", djService.getAllDJs());
        // наприклад, у шаблоні "index.html": <tr th:each="dj : ${djs}"> динамічно рендерить таблицю з діджеями

        // RedirectAttributes - Spring MVC об'єкт для "flash attributes" (тимчасові атрибути, що зберігаються лише для наступного редіректу,
        // реалізовано через сесію HTTP, зникають після першого запиту).
        // використовується для PRG-патерну (Post-Redirect-Get): після POST (/add) редіректимо на GET (/), і повідомлення "проноситься" без повторного POST.

        // показати flash-повідомлення, якщо є (error або success)
        if (redirectAttributes.getFlashAttributes().containsKey("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }
        if (redirectAttributes.getFlashAttributes().containsKey("success")) {
            model.addAttribute("success", redirectAttributes.getFlashAttributes().get("success"));
        } // в шаблоні це корисно для показу повідомлень користувачу, <div th:if="${error}" class="alert alert-error" th:text="${error}">Помилка!</div>
        return "index";
    }

    // обробка post-запиту для додавання
    @PostMapping("/add") // мапінг на /add для веб-форми (HTML POST з форми <form action="/add">)
    public String addDJ(@RequestParam("name") @NotBlank @Size(max = 50) String name,
                        @RequestParam("rank") @Min(1) int rank, RedirectAttributes redirectAttributes) {
        System.out.println("POST /add викликано!"); // лог для перевірки
        try {
            var newDJ = new DJ(name, rank);
            djService.addDJ(newDJ);
            redirectAttributes.addFlashAttribute("success",
                    "Діджея '" + name + "' успішно додано з рейтингом " + rank + "!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/"; // команда для HTTP-редіректу (статус 302 Found) на корінь додатку (/).
        // виконується після POST (наприклад, після форми) - редіректить на GET-запит до / (index-метод).
        // це реалізація PRG-патерну (Post-Redirect-Get) для уникнення повторного сабміту форми при оновленні сторінки.
    }
}

// глобальний обробник помилок
@ControllerAdvice
class GlobalExceptionHandler {

    // обробка 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handle404(NoHandlerFoundException e) {
        return new ResponseEntity<>("помилка 404: сторінку не знайдено", HttpStatus.NOT_FOUND);
    }

    // обробка відсутнього ресурсу
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // обробка валідації для параметрів
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleValidation(BindException e) {
        return new ResponseEntity<>("помилка валідації: " + e.getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST);
    }

    // обробка illegalargument (для api та інших, не для web-post)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>("помилка валідації: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // обробка інших помилок
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception e) {  // тепер повертаємо ModelAndView, не ResponseEntity
        ModelAndView mav = new ModelAndView("error");  // "error" — назва шаблону (src/main/resources/templates/error.html)

        // додаємо атрибути в модель для шаблону (доступні як ${message}, ${type} у Thymeleaf)
        mav.addObject("message", "Виникла помилка: " + e.getMessage());  // основне повідомлення
        mav.addObject("type", e.getClass().getSimpleName());  // тип помилки (наприклад, "NullPointerException")
        mav.addObject("timestamp", java.time.LocalDateTime.now());  // поточний час для логування/показу

        // встановлюємо HTTP-статус (500 для INTERNAL_SERVER_ERROR)
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return mav;  // Spring резолвер (ThymeleafViewResolver) рендерить error.html з даними
    }
}
*/



