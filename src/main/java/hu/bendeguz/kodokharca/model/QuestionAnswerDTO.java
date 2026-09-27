package hu.bendeguz.kodokharca.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {
    
    public static final String INDEX_SEPARATOR = ", ";
    public static final String MISSING_INDEX = "-";
    
    // TODO: 2023. 10. 16. create and use an enum which indicates what type of value this DTO holds
    
    private Boolean isTrue;
    private Integer count;
    private Integer sum;
    private Integer diff;
    private List<Integer> indexes;
    
    
    public String getValue() {
        if (isTrue != null) {
            return String.valueOf(isTrue);
        }
        
        if (count != null) {
            return String.valueOf(count);
        }
        
        if (sum != null) {
            return String.valueOf(sum);
        }
        
        if (diff != null) {
            return String.valueOf(diff);
        }
        
        if (indexes != null) {
            if (indexes.isEmpty()) {
                return MISSING_INDEX;
            }
            return indexes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(INDEX_SEPARATOR));
        }
        throw new IllegalStateException("All of the values are null!");
    }
    
    @Override
    public String toString() {
        return String.format("QuestionAnswerDTO{%s}", getValue());
    }
    
    public static QuestionAnswerDTO ofIsTrue(Boolean isTrue) {
        return builder().isTrue(isTrue).build();
    }
    
    public static QuestionAnswerDTO ofCount(Integer count) {
        return builder().count(count).build();
    }
    
    public static QuestionAnswerDTO ofSum(Integer sum) {
        return builder().sum(sum).build();
    }
    
    public static QuestionAnswerDTO ofDiff(Integer diff) {
        return builder().diff(diff).build();
    }
    
    public static QuestionAnswerDTO ofIndexes(List<Integer> indexes) {
        return builder().indexes(indexes).build();
    }
}
