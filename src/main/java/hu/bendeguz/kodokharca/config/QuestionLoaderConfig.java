package hu.bendeguz.kodokharca.config;

import hu.bendeguz.kodokharca.model.Question;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class QuestionLoaderConfig {
    
    @Value("classpath:/templates/fragments/questions")
    private Resource resource;
    
    @Bean("questionIds")
    public List<String> questionIds() throws IOException {
        return Arrays.stream(resource.getFile().list())
            .map(s -> s.replaceAll("\\.html", ""))
            .filter(Predicate.not(s -> s.equals("guess"))) // guessing is not a question
            .collect(Collectors.toUnmodifiableList());
    }
    
    @Bean("questionIdsAndTexts")
    public Map<String, String> questionIdsAndTexts() throws IOException {
        Map<String, String> questionIdsAndTexts = new HashMap<>();
        File[] files = resource.getFile().listFiles();
        
        for (File file : files) {
            String question =
                Files.lines(file.toPath())
                    .filter(line -> {
                            line = line.strip();
                            return line.matches("<p.*</p>");
                        })
                    .map(line -> {
                            line = line.strip();
                            line = line.replaceAll("<p.*?>", ""); // *? -> match as few as possible
                            line = line.replaceAll("</p>", "");
                            return line;
                        })
                    .findFirst()
                    .orElseThrow();
            
            String questionName = file.getName().replaceAll("\\.html", "");
            questionIdsAndTexts.put(questionName, question);
        }
        
        Map<String, String> map = Collections.unmodifiableMap(questionIdsAndTexts);
        Question.questionIdsAndTexts = map;
        
        return map;
    }
}
