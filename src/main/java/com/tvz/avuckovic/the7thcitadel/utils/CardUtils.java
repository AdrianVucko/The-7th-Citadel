package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.model.Card;
import com.tvz.avuckovic.the7thcitadel.model.CardBackColor;
import com.tvz.avuckovic.the7thcitadel.model.CardFlag;
import com.tvz.avuckovic.the7thcitadel.model.CardType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUtils {
    private static final int NUMBER_OF_CARDS = 5;

    public static List<Card> drawShuffledCards(List<Card> deck) {
        if (deck == null || deck.isEmpty()) {
            throw new IllegalArgumentException("Deck cannot be null or empty.");
        }

        List<Card> shuffledDeck = new ArrayList<>(deck);
        Collections.shuffle(shuffledDeck);
        return shuffledDeck.subList(0, Math.min(NUMBER_OF_CARDS, shuffledDeck.size()));
    }

    public static List<Card> loadCards() {
        File file = new File("dat/cards.ser");
        if(!file.exists()) {
            return saveCards();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dat/cards.ser"))) {
            return (List<Card>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An error occured while loading cards!", e);
        }
    }

    private static List<Card> saveCards() {
        try (BufferedReader reader = new BufferedReader(new FileReader("dat/cards.txt"));
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat/cards.ser"))) {
            List<Card> cards = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                //Empty string if there is no entry for column
                line = line.replaceAll(";(?=;|$)", "; ");
                String[] cardAttributes = line.split(";");

                cards.add(buildCardFromAttributes(cardAttributes));
            }

            out.writeObject(cards);
            return cards;
        } catch (IOException e) {
            throw new RuntimeException("An error occured while saving cards!", e);
        }
    }

    private static Card buildCardFromAttributes(String[] cardAttributes) {
        if(cardAttributes.length != 5) {
            throw new RuntimeException("card data not split correctly");
        }
        return Card.builder()
                .id(cardAttributes[0])
                .type(CardType.valueOf(cardAttributes[1]))
                .description(cardAttributes[2])
                .backColor(CardBackColor.valueOf(cardAttributes[3]))
                .flag(CardFlag.valueOf(cardAttributes[4]))
                .build();
    }
}
