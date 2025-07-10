package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
                .collect(Collectors.toList());
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

    public static List<Card> loadCards() {
        if(!FileUtils.fileExists("dat/cards.ser")) {
            return saveCards();
        }
        return FileUtils.loadObjectsFromFile("dat/cards.ser");
    }

    private static List<Card> saveCards() {
        List<Card> cards = FileUtils
                .readRowAttributesForFile("dat/cards.txt", false).stream()
                .map(CardUtils::buildCardFromAttributes)
                .collect(Collectors.toList());
        FileUtils.writeObjects("dat/cards.ser", cards);
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
