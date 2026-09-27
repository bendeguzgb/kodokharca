package hu.bendeguz.kodokharca.service.question;

import java.math.BigInteger;
import java.util.Random;
import org.springframework.util.StringUtils;

public class GameCodeGenerator {

    private static final char[] UPPER_CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    
    
    public static String createCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 3; i++) {
            code.append(nextUpperChar(random));
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
    
    public static Random createRandom(int playerCount, String code) {
        if (!StringUtils.hasLength(code)) {
            code = createCode();
        }
        
        code = playerCount + code;

        // "09AZaz" -> [48, 57, 65, 90, 97, 122] -> "4857659097122"
        String strSeed = code.chars()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

        // will be negative if seed is bigger than LONG.MAX_VALUE (which is not a problem)
        long seed = new BigInteger(strSeed).longValue();
        return new Random(seed);
    }
    
    private static String nextUpperChar(Random random) {
        int index = random.nextInt(UPPER_CHARS.length);
        return String.valueOf(UPPER_CHARS[index]);
    }
}
