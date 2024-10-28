import java.io.*;
import java.util.*;

public class PL {

    enum TokenType {
        SAYI, ID, NUMBER, ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE,
        IF, ELSE, WHILE, LPAREN, RPAREN, GREATER, LESS, EOF, UNKNOWN
    }

    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + " " + value;
        }
    }

    static class Lexer {
        private BufferedReader reader;
        private int currentChar;
        private String currentLine;
        private int currentPos;
        private List<Token> tokens;

        public Lexer(String fileName) throws IOException {
            reader = new BufferedReader(new FileReader(fileName));
            tokens = new ArrayList<>();
            currentPos = -1;
            readNextLine();
        }

        private void readNextLine() throws IOException {
            currentLine = reader.readLine();
            currentPos = -1;
            advance();
        }

        private void advance() {
            if (currentLine != null) {
                currentPos++;
                if (currentPos >= currentLine.length()) {
                    currentChar = -1;
                } else {
                    currentChar = currentLine.charAt(currentPos);
                }
            } else {
                currentChar = -1;
            }
        }

        public List<Token> tokenize() throws IOException {
            while (currentLine != null) {
                while (currentChar != -1) {
                    if (Character.isWhitespace(currentChar)) {
                        advance();
                    } else if (Character.isDigit(currentChar)) {
                        tokens.add(number());
                    } else if (isLetter(currentChar)) {
                        tokens.add(identifier());
                    } else if (currentChar == '=') {
                        tokens.add(new Token(TokenType.ASSIGN, "="));
                        advance();
                    } else if (currentChar == '+') {
                        tokens.add(new Token(TokenType.PLUS, "+"));
                        advance();
                    } else if (currentChar == '-') {
                        tokens.add(new Token(TokenType.MINUS, "-"));
                        advance();
                    } else if (currentChar == '*') {
                        tokens.add(new Token(TokenType.MULTIPLY, "*"));
                        advance();
                    } else if (currentChar == '/') {
                        tokens.add(new Token(TokenType.DIVIDE, "/"));
                        advance();
                    } else if (currentChar == '(') {
                        tokens.add(new Token(TokenType.LPAREN, "("));
                        advance();
                    } else if (currentChar == ')') {
                        tokens.add(new Token(TokenType.RPAREN, ")"));
                        advance();
                    } else if (currentChar == '>') {
                        tokens.add(new Token(TokenType.GREATER, ">"));
                        advance();
                    } else if (currentChar == '<') {
                        tokens.add(new Token(TokenType.LESS, "<"));
                        advance();
                    } else {
                        tokens.add(new Token(TokenType.UNKNOWN, Character.toString(currentChar)));
                        advance();
                    }
                }
                readNextLine();
            }
            tokens.add(new Token(TokenType.EOF, ""));
            return tokens;
        }

        private Token number() {
            StringBuilder value = new StringBuilder();
            while (currentChar != -1 && Character.isDigit(currentChar)) {
                value.append((char) currentChar);
                advance();
            }
            return new Token(TokenType.NUMBER, value.toString());
        }

        private Token identifier() {
            StringBuilder value = new StringBuilder();
            while (currentChar != -1 && (isLetter(currentChar) || Character.isDigit(currentChar))) {
                value.append((char) currentChar);
                advance();
            }
            String stringValue = value.toString();
            if (stringValue.equals("sayi")) return new Token(TokenType.SAYI, stringValue);
            if (stringValue.equals("ise")) return new Token(TokenType.IF, stringValue);
            if (stringValue.equals("değilse")) return new Token(TokenType.ELSE, stringValue);
            if (stringValue.equals("sürece")) return new Token(TokenType.WHILE, stringValue);
            return new Token(TokenType.ID, stringValue);
        }

        private boolean isLetter(int c) {
            return Character.isLetter(c) || "ğĞüÜşŞöÖçÇıİ".indexOf(c) != -1;
        }

        public void printTokens() {
            for (Token token : tokens) {
                System.out.println(token);
            }
        }
    }

    static class Parser {
        private List<Token> tokens;
        private int currentTokenIndex;
        private Map<String, Integer> symbolTable;
        private StringBuilder parseTree;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
            this.currentTokenIndex = 0;
            this.symbolTable = new HashMap<>();
            this.parseTree = new StringBuilder();
        }

        private Token currentToken() {
            return tokens.get(currentTokenIndex);
        }

        private void advance() {
            if (currentTokenIndex < tokens.size() - 1) {
                currentTokenIndex++;
            }
        }

        private boolean match(TokenType type) {
            if (currentToken().type == type) {
                advance();
                return true;
            }
            return false;
        }

        public void parse() {
            while (currentToken().type != TokenType.EOF) {
                statement();
            }
        }

        private void statement() {
            if (match(TokenType.SAYI)) {
                parseTree.append("<degiskenBildirimi> -> ");
                declaration();
                parseTree.append("<degiskenBildirimi>\n");
            } else if (currentToken().type == TokenType.ID) {
                parseTree.append("<atama> -> ");
                assignment();
                parseTree.append("<atama>\n");
            } else if (match(TokenType.IF)) {
                parseTree.append("<ifDeyimi> -> ");
                System.out.println(currentToken());
                System.out.println("<ifDeyimi> girildi");
                ifStatement();
                System.out.println("<ifDeyimi> cikildi");
                parseTree.append("<ifDeyimi>\n");
            } else if (match(TokenType.WHILE)) {
                parseTree.append("<whileDeyimi> -> ");
                System.out.println(currentToken());
                whileStatement();
                parseTree.append("<whileDeyimi>\n");
            } else {
                advance();  // Skip unknown tokens
            }
        }

        private void declaration() {
            if (currentToken().type == TokenType.ID) {
                parseTree.append(currentToken().value + " ");
                System.out.println(currentToken());
                String varName = currentToken().value;
                advance();
                if (match(TokenType.ASSIGN)) {
                    parseTree.append("= <ifade> ");
                    System.out.println("EQUAL =");
                    System.out.println("<ifade> girildi");
                    int value = expression();
                    symbolTable.put(varName, value);
                    System.out.println("<ifade> cikildi");
                }
            }
        }

        private void assignment() {
            parseTree.append(currentToken().value + " ");
            System.out.println(currentToken());
            String varName = currentToken().value;
            advance();
            if (match(TokenType.ASSIGN)) {
                parseTree.append("= <ifade> ");
                System.out.println("EQUAL =");
                System.out.println("<ifade> girildi");
                int value = expression();
                symbolTable.put(varName, value);
                System.out.println("<ifade> cikildi");
            }
        }

        private int expression() {
            int result = term();
            while (currentToken().type == TokenType.PLUS || currentToken().type == TokenType.MINUS) {
                if (currentToken().type == TokenType.PLUS) {
                    parseTree.append("+ ");
                    System.out.println(currentToken());
                    match(TokenType.PLUS);
                    result += term();
                } else if (currentToken().type == TokenType.MINUS) {
                    parseTree.append("- ");
                    System.out.println(currentToken());
                    match(TokenType.MINUS);
                    result -= term();
                }
            }
            return result;
        }

        private int term() {
            int result = factor();
            while (currentToken().type == TokenType.MULTIPLY || currentToken().type == TokenType.DIVIDE) {
                if (currentToken().type == TokenType.MULTIPLY) {
                    parseTree.append("* ");
                    System.out.println(currentToken());
                    match(TokenType.MULTIPLY);
                    result *= factor();
                } else if (currentToken().type == TokenType.DIVIDE) {
                    parseTree.append("/ ");
                    System.out.println(currentToken());
                    match(TokenType.DIVIDE);
                    result /= factor();
                }
            }
            return result;
        }

        private int factor() {
            int result = 0;
            if (currentToken().type == TokenType.NUMBER) {
                parseTree.append(currentToken().value + " ");
                System.out.println(currentToken());
                result = Integer.parseInt(currentToken().value);
                advance();
            } else if (currentToken().type == TokenType.ID) {
                parseTree.append(currentToken().value + " ");
                System.out.println(currentToken());
                String varName = currentToken().value;
                advance();
                result = symbolTable.getOrDefault(varName, 0);
            } else if (match(TokenType.LPAREN)) {
                parseTree.append("( <ifade> ");
                System.out.println(currentToken());
                result = expression();
                if (!match(TokenType.RPAREN)) {
                    error("Expected closing parenthesis");
                }
                parseTree.append(") ");
                System.out.println(currentToken());
            }
            return result;
        }

        private void ifStatement() {
            if (match(TokenType.LPAREN)) {
                parseTree.append("( <kosul> ) ");
                System.out.println("<ise> girildi");
                boolean condition = condition();
                if (!match(TokenType.RPAREN)) {
                    error("Expected closing parenthesis");
                }

                if (condition) {
                    statement(); // Execute the statement inside the if block
                    // Skip the else block if condition is true
                    while (!match(TokenType.ELSE) && currentToken().type != TokenType.EOF) {
                        advance();
                    }
                    if (match(TokenType.ELSE)) {
                        // Skip the else block
                        while (currentToken().type != TokenType.EOF) {
                            advance();
                        }
                    }
                    System.out.println("<ise> cikildi");
                } else {
                    // Skip the statements in the if block
                    while (currentToken().type != TokenType.ELSE && currentToken().type != TokenType.EOF) {
                        advance();
                    }
                    System.out.println("<ise> cikildi");
                    if (match(TokenType.ELSE)) {
                        System.out.println("<degilse> girildi");
                        statement(); // Execute the statement inside the else block
                        System.out.println("<degilse> cikildi");
                    }
                }
            } else {
                error("Expected opening parenthesis for if statement");
            }
        }

        private void whileStatement() {
            if (match(TokenType.LPAREN)) {
                parseTree.append("( <kosul> ) <dongu> ");
                System.out.println("<whileDeyimi> girildi");
                int conditionStartIndex = currentTokenIndex; // Remember the start index for the condition
                boolean condition = condition();
                if (!match(TokenType.RPAREN)) {
                    error("Expected closing parenthesis");
                }

                // Remember the start index for the loop body
                int bodyStartIndex = currentTokenIndex;

                while (condition) {
                    currentTokenIndex = bodyStartIndex;
                    while (currentTokenIndex < tokens.size() && currentToken().type != TokenType.EOF && currentToken().type != TokenType.WHILE) {
                        statement();
                    }
                    currentTokenIndex = conditionStartIndex; // Reset to re-evaluate the condition
                    condition = condition();
                }
                System.out.println("<whileDeyimi> cikildi");
            } else {
                error("Expected opening parenthesis for while statement");
            }
        }

        private boolean condition() {
            int left = expression();
            if (currentToken().type == TokenType.GREATER) {
                parseTree.append("> ");
                System.out.println(currentToken());
                advance();
                int right = expression();
                return left > right;
            } else if (currentToken().type == TokenType.LESS) {
                parseTree.append("< ");
                System.out.println(currentToken());
                advance();
                int right = expression();
                return left < right;
            }
            return false;
        }

        private void error(String message) {
            System.err.println("Error: " + message + " at token " + currentToken());
            System.exit(1);
        }

        public void printParseTree() {
            System.out.println("BNF FORMAT");
            System.out.println(parseTree.toString());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = "";
        boolean fileLoaded = false;

        while (!fileLoaded) {
            System.out.print("Okunmasini istediginiz text dosyasi numarasini yazin: ");
            String userInput = scanner.nextLine();

            String currentDirectory = System.getProperty("user.dir");
            filePath = currentDirectory + "/src/sample" + userInput + ".txt";

            try {
                Lexer lexer = new Lexer(filePath);
                List<Token> tokens = lexer.tokenize();
                for (Token token : tokens) {
                    System.out.println(token);
                }
                Parser parser = new Parser(tokens);
                parser.parse();
                parser.printParseTree();
                fileLoaded = true;
            } catch (FileNotFoundException e) {
                System.out.println("File not found. Please try again.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}