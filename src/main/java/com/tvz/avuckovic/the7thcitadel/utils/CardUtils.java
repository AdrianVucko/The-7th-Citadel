package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUtils {
    public static List<Card> drawShuffledActionCards(List<Card> deck) {
        if (deck == null || deck.isEmpty()) {
            throw new IllegalArgumentException("Deck cannot be null or empty.");
        }

        List<Card> shuffledActionDeck = deck.stream()
                .filter(card -> card.getType().equals(CardType.ACTION))
                .collect(Collectors.toList());
        Collections.shuffle(shuffledActionDeck);
        return shuffledActionDeck.subList(0, Math.min(GameConstants.Player.CARDS_IN_HAND, shuffledActionDeck.size()));
    }

    public static List<Card> loadCardsByType(CardType cardType) {
        return loadCards().stream()
                .filter(card -> card.getType().equals(cardType))
                .collect(Collectors.toList());
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
            throw new IllegalStateException("card data not split correctly");
        }
        String description = cardAttributes[2];
        return Card.builder()
                .id(cardAttributes[0])
                .type(CardType.valueOf(cardAttributes[1]))
                .description(description)
                .skillType(evaluateSkillType(description))
                .explorationArea(evaluateExplorationArea(description))
                .backColor(CardBackColor.valueOf(cardAttributes[3]))
                .flag(CardFlag.valueOf(cardAttributes[4]))
                .build();
    }

    private static SkillType evaluateSkillType(String description) {
        String[] words = description.split(" ");
        String firstWord = words[0];
        return Arrays.stream(SkillType.values())
                .filter(skillType -> skillType.name().equals(firstWord.toUpperCase()))
                .findFirst()
                .orElse(SkillType.NONE);
    }

    private static ExplorationArea evaluateExplorationArea(String description) {
        return Arrays.stream(ExplorationArea.values())
                .filter(explorationArea -> explorationArea.getFullDescription().equals(description))
                .findFirst()
                .orElse(ExplorationArea.NONE);
    }
}
