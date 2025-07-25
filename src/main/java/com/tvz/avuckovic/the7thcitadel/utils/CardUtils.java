package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUtils {

    private static final String CARDS_FILE = "dat/cards.ser";

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

    public static Card selectUnusedRandomSkill(List<Card> allCards, List<Card> playerCards) {
        List<Card> unused = new ArrayList<>(allCards).stream()
                .filter(card -> card.getType().equals(CardType.ACTION))
                .collect(Collectors.toList());
        unused.removeAll(playerCards);

        if (unused.isEmpty()) {
            throw new ApplicationException(Message.NO_UNUSED_CARDS.getText());
        }

        Collections.shuffle(unused);
        return unused.get(0);
    }

    public static List<Card> removeSkillCardsFromPlayer(SkillType skillType, int numberOfCards) {
        Player player = Player.getInstance();
        List<Card> cardsForDiscard = player.getActionDeck().stream()
                .filter(card -> card.getSkillType().equals(skillType))
                .limit(numberOfCards)
                .toList();
        for (Card cardForDiscard : cardsForDiscard) {
            player.getActionDeck().remove(cardForDiscard);
            player.getDiscardPile().add(cardForDiscard);
        }
        return cardsForDiscard;
    }

    public static String assignPlayerName(List<Card> allCards) {
        List<Card> characterCards = allCards.stream()
                .filter(card -> card.getType().equals(CardType.CHARACTER))
                .collect(Collectors.toList());
        Collections.shuffle(characterCards);
        return characterCards.isEmpty() ? TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name() :
                characterCards.get(0).getDescription();
    }

    public static int evaluateAreaNumber(int cellNumber) {
        BigDecimal numberOfFields = new BigDecimal(GameConstants.Board.ROWS * GameConstants.Board.COLS);
        int split = numberOfFields.divide(new BigDecimal("10"), 0, RoundingMode.FLOOR).intValue();
        for(int i = 0; i < 10; i++) {
            if(cellNumber < ((i+1) * split)) {
                return i;
            }
        }
        return 10;
    }

    public static ExplorationArea evaluateExplorationAreaByNumber(int areaNumber) {
        return switch (areaNumber) {
            case 0 -> ExplorationArea.FIRST;
            case 1 -> ExplorationArea.SECOND;
            case 2 -> ExplorationArea.THIRD;
            case 3 -> ExplorationArea.FOURTH;
            case 4 -> ExplorationArea.FIFTH;
            case 5 -> ExplorationArea.SIXTH;
            case 6 -> ExplorationArea.SEVENTH;
            case 7 -> ExplorationArea.EIGHT;
            case 8 -> ExplorationArea.NINTH;
            case 9 -> ExplorationArea.TENTH;
            default -> ExplorationArea.NONE;
        };
    }

    public static int evaluateAreaNumberByExplorationArea(ExplorationArea explorationArea) {
        return switch (explorationArea) {
            case FIRST -> 0;
            case SECOND -> 1;
            case THIRD -> 2;
            case FOURTH -> 3;
            case FIFTH -> 4;
            case SIXTH -> 5;
            case SEVENTH -> 6;
            case EIGHT -> 7;
            case NINTH -> 8;
            case TENTH -> 9;
            default -> 10;
        };
    }

    public static List<Card> loadCards() {
        if(!FileUtils.fileExists(CARDS_FILE)) {
            return saveCards();
        }
        return FileUtils.loadObjectsFromFile(CARDS_FILE);
    }

    private static List<Card> saveCards() {
        List<Card> cards = FileUtils
                .readRowAttributesForFile("dat/cards.txt", false).stream()
                .map(CardUtils::buildCardFromAttributes)
                .toList();
        FileUtils.writeObjects(CARDS_FILE, cards);
        return cards;
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
        if(description.equals("Area intro")) {
            return ExplorationArea.FIRST;
        }
        return Arrays.stream(ExplorationArea.values())
                .filter(explorationArea -> explorationArea.getFullDescription().equals(description))
                .findFirst()
                .orElse(ExplorationArea.NONE);
    }
}
