package impl.expression;

import api.expression.ExpressionParser;
import api.expression.ParseException;

import java.util.Optional;

public class ExpressionParserImpl implements ExpressionParser {

    @Override
    public int parse(String expression) throws ParseException {
        if (expression == null) {
            throw new IllegalArgumentException("Expression may not be null");
        }
        expression = expression.replaceAll("\\s", "");
        ExpressionProcessor processor = new ExpressionProcessor();
        for (int index = 0; index < expression.length(); index++) {
            char character = expression.charAt(index);
            if ('0' <= character && character <= '9') {
                processor.processDigit(character);
            } else if (character == '+') {
                processor.processSign(Sign.PLUS);
            } else if (character == '-') {
                processor.processSign(Sign.MINUS);
            } else {
                String message = String.format("Illegal character '%c' encountered at index %d", character, index);
                throw new ParseException(message);
            }
        }
        return processor.getResult();
    }

    private enum Sign {
        PLUS,
        MINUS
    }

    private static class ExpressionProcessor {

        private int result = 0;
        private Sign sign;
        private StringBuilder number;

        public void processDigit(char digit) {
            if (number == null) {
                number = new StringBuilder();
            }
            number.append(digit);
        }

        public void processSign(Sign newSign) throws ParseException {
            executeAddition();
            sign = newSign;
            number = null;
        }

        public int getResult() throws ParseException {
            executeAddition();
            return result;
        }

        private void executeAddition() throws ParseException {
            Optional<Integer> currentNumber = getCurrentNumber();
            if (currentNumber.isPresent()) {
                if (sign == Sign.MINUS) {
                    result -= currentNumber.get();
                } else {
                    result += currentNumber.get();
                }
            } else if (sign != null) {
                throw new ParseException("Several signs in a row are not permitted");
            }
        }

        private Optional<Integer> getCurrentNumber() throws ParseException {
            if (number == null || number.length() == 0) {
                return Optional.empty();
            }
            try {
                return Optional.of(Integer.parseInt(number.toString()));
            } catch (Exception e) {
                throw new ParseException("Unexpected number: " + e.getMessage());
            }
        }
    }
}
