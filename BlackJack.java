import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {

    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AQKJ".contains(value)) {
                if (value.equals("A")) return 11;
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImgPath() {
            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();

    // Dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // Player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // Window dimensions
    int width = 1400;
    int height = width;

    int cardWidth = 220;
    int cardHeight = 308;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw hidden card
            Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
            if (!stayButton.isEnabled()) {
                hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImgPath())).getImage();
            }
            g.drawImage(hiddenCardImg, 40, 40, cardWidth, cardHeight, null);

            // Draw dealer's hand
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                Image cardImage = new ImageIcon(getClass().getResource(card.getImgPath())).getImage();
                g.drawImage(cardImage, cardWidth + 50 + (cardWidth + 10) * i, 40, cardWidth, cardHeight, null);
            }

            // Draw player's hand
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image cardImage = new ImageIcon(getClass().getResource(card.getImgPath())).getImage();
                g.drawImage(cardImage, 40 + (cardWidth + 10) * i, 640, cardWidth, cardHeight, null);
            }

            if (!stayButton.isEnabled()) {
                dealerSum = reduceDealerAce();
                playerSum = reducePlayerAce();

                String message = "";
                if (playerSum > 21) {
                    message = "You Lose!";
                } else if (dealerSum > 21) {
                    message = "You Win!";
                } else if (playerSum == dealerSum) {
                    message = "Tie!";
                } else if (playerSum > dealerSum) {
                    message = "You Win!";
                } else if (playerSum < dealerSum) {
                    message = "You Lose!";
                }

                g.setFont(new Font("Arial", Font.PLAIN, 60));
                g.setColor(Color.white);
                g.drawString(message, 500, 500);
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton resetButton = new JButton("Reset");

    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setPreferredSize(new Dimension(width, height - 150)); // Adjusted height for game panel
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel, BorderLayout.CENTER);

        buttonPanel.setPreferredSize(new Dimension(width, 100)); // Set preferred size for button panel

        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        hitButton.setPreferredSize(new Dimension(150, 50)); // Increase button size
        hitButton.setFont(buttonFont); // Set font size
        stayButton.setPreferredSize(new Dimension(150, 50)); // Increase button size
        stayButton.setFont(buttonFont); // Set font size
        resetButton.setPreferredSize(new Dimension(150, 50)); // Increase button size
        resetButton.setFont(buttonFont); // Set font size

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        resetButton.setFocusable(false);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "H", "D", "S"};
        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

}

