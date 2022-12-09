package hu.bendeguz.kodokharca.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class GameNumber {

    @Setter(AccessLevel.NONE)
    private int value;

    @Setter(AccessLevel.NONE)
    private Color color;

}
