import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin
public class Example {

    @RequestMapping("/welcome")
    String home() {
        return "Welcome from server!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

}
