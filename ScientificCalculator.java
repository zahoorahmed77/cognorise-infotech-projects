import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator {
    private JFrame frame;
    private JTextField display;
    private String input = "";
    public ScientificCalculator() {
        frame = new JFrame("Scientific Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        display = new JTextField();
        display.setFont(new Font("Arial", Font.PLAIN, 24));
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLUE);
        frame.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 5, 5, 5));
        buttonPanel.setBackground(Color.WHITE);

        String[] buttons = {
                "7", "8", "9", "/", "sqrt",
                "4", "5", "6", "*", "x^2",
                "1", "2", "3", "-", "x^y",
                "0", ".", "+/-", "+", "=",
                "(", ")", "sin", "cos", "tan",
                "log", "ln", "C", "DEL"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setForeground(Color.BLUE);
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if ("0123456789.".contains(command)) {
                input += command;
            } else if ("+-*/()".contains(command)) {
                input += " " + command + " ";
            } else if ("sqrt x^2 x^y sin cos tan log ln".contains(command)) {
                input += " " + command + "(";
            } else if ("=".equals(command)) {
                try {
                    input = String.valueOf(evaluateExpression(input));
                } catch (Exception ex) {
                    input = "Error";
                }
            } else if ("+/-".equals(command)) {
                input = negateInput(input);
            } else if ("C".equals(command)) {
                input = "";
            } else if ("DEL".equals(command)) {
                if (!input.isEmpty()) {
                    input = input.substring(0, input.length() - 1);
                }
            }

            display.setText(input);
        }
    }

    private double evaluateExpression(String expression) {
        return new ExpressionParser().parse(expression);
    }

    private String negateInput(String input) {
        if (input.isEmpty())
            return input;

        String[] parts = input.split(" ");
        int lastIndex = parts.length - 1;
        String lastPart = parts[lastIndex];

        if (!lastPart.isEmpty() && Character.isDigit(lastPart.charAt(0))) {
            if (lastPart.charAt(0) == '-') {
                parts[lastIndex] = lastPart.substring(1);
            } else {
                parts[lastIndex] = "-" + lastPart;
            }
            return String.join(" ", parts);
        } else {
            return input;
        }
    }

    private static class ExpressionParser {
        private int pos = -1;
        private int ch;

        private void nextChar(String expression) {
            ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
        }

        private boolean eat(int charToEat, String expression) {
            while (ch == ' ')
                nextChar(expression);
            if (ch == charToEat) {
                nextChar(expression);
                return true;
            }
            return false;
        }

        private double parse(String expression) {
            nextChar(expression);
            double x = parseExpression(expression);
            if (pos < expression.length())
                throw new RuntimeException("Unexpected: " + (char) ch);
            return x;
        }

        private double parseExpression(String expression) {
            double x = parseTerm(expression);
            while (true) {
                if (eat('+', expression))
                    x += parseTerm(expression);
                else if (eat('-', expression))
                    x -= parseTerm(expression);
                else
                    return x;
            }
        }

        private double parseTerm(String expression) {
            double x = parseFactor(expression);
            while (true) {
                if (eat('*', expression))
                    x *= parseFactor(expression);
                else if (eat('/', expression))
                    x /= parseFactor(expression);
                else if (eat('^', expression))
                    x = Math.pow(x, parseFactor(expression));
                else
                    return x;
            }
        }

        private double parseFactor(String expression) {
            if (eat('+', expression))
                return parseFactor(expression);
            if (eat('-', expression))
                return -parseFactor(expression);

            double x;
            int startPos = this.pos;
            if (eat('(', expression)) {
                x = parseExpression(expression);
                eat(')', expression);
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.')
                    nextChar(expression);
                x = Double.parseDouble(expression.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') {
                while (ch >= 'a' && ch <= 'z')
                    nextChar(expression);
                String func = expression.substring(startPos, this.pos);
                x = parseFactor(expression);
                switch (func) {
                    case "sqrt":
                        x = Math.sqrt(x);
                        break;
                    case "log":
                        x = Math.log10(x);
                        break;
                    case "ln":
                        x = Math.log(x);
                        break;
                    case "sin":
                        x = Math.sin(Math.toRadians(x));
                        break;
                    case "cos":
                        x = Math.cos(Math.toRadians(x));
                        break;
                    case "tan":
                        x = Math.tan(Math.toRadians(x));
                        break;
                    case "asin":
                        x = Math.toDegrees(Math.asin(x));
                        break;
                    case "acos":
                        x = Math.toDegrees(Math.acos(x));
                        break;
                    case "atan":
                        x = Math.toDegrees(Math.atan(x));
                        break;
                    default:
                        throw new RuntimeException("Unknown function: " + func);
                }
            } else {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            return x;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}