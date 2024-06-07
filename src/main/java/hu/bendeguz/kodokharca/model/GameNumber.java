package hu.bendeguz.kodokharca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
public class GameNumber implements Comparable<GameNumber> {
    
    private int value;
    private Color color;
    
    @JsonIgnore
    public boolean isBlackNumber() {
        return color == Color.BLACK;
    }
    
    @JsonIgnore
    public boolean isWhiteNumber() {
        return color == Color.WHITE;
    }
    
    @JsonIgnore
    public boolean isGreenNumber() {
        return color == Color.GREEN;
    }
    
    @Override
    public int compareTo(GameNumber o) {
        int intCompare = Integer.compare(this.value, o.value);
        
        if (intCompare != 0) {
            return intCompare;
        }
        return this.color.compareTo(o.getColor());
    }
}
