package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.model.Card;
import com.tvz.avuckovic.the7thcitadel.model.GameMove;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlUtils {
    public static final String GAME_MOVES_XML_FILE_NAME = "dat/gameMoves.xml";

    public static Optional<GameMove> readLastGameMove() {
        try {
            if(!FileUtils.fileExists(GAME_MOVES_XML_FILE_NAME)) {
                return Optional.empty();
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(new File(GAME_MOVES_XML_FILE_NAME));

            Element rootElement = xmlDoc.getDocumentElement();
            NodeList gameMovesNodeList = rootElement.getElementsByTagName("GameMove");
            if(gameMovesNodeList.getLength() == 0) {
                return Optional.empty();
            }
            return Optional.of(buildGameMoveFromElement((Element) gameMovesNodeList.item(gameMovesNodeList.getLength() - 1)));
        } catch(Exception e) {
            throw new ConfigurationException("There was an error while reading XML file with game moves.", e);
        }
    }

    public static void saveGameMovesToXmlFile(List<GameMove> gameMoves) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.newDocument();

            Element rootElement = xmlDoc.createElement("GameMoves");
            xmlDoc.appendChild(rootElement);

            for (GameMove gameMove : gameMoves) {
                Element gameMoveElement = xmlDoc.createElement("GameMove");

                Element playerOne = createPlayerElement(xmlDoc, "playerOne", gameMove.getPlayerOne());
                Element playerTwo = createPlayerElement(xmlDoc, "playerTwo", gameMove.getPlayerTwo());
                Element completedFields = xmlDoc.createElement("completedFields");

                for (Integer completedField : gameMove.getCompletedFields()) {
                    Element cellNumber = xmlDoc.createElement("cellNumber");
                    cellNumber.setTextContent(String.valueOf(completedField));
                    completedFields.appendChild(cellNumber);
                }

                gameMoveElement.appendChild(playerOne);
                gameMoveElement.appendChild(playerTwo);
                gameMoveElement.appendChild(completedFields);

                rootElement.appendChild(gameMoveElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(xmlDoc);
            StreamResult streamResult = new StreamResult(new File(GAME_MOVES_XML_FILE_NAME));
            transformer.transform(domSource, streamResult);
        } catch(Exception e) {
            throw new ConfigurationException("There was an error while creating XML file with game moves.", e);
        }
    }

    public static List<GameMove> readGameMovesFromXmlFile() {
        List<GameMove> gameMoves = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(new File(GAME_MOVES_XML_FILE_NAME));

            Element rootElement = xmlDoc.getDocumentElement();
            NodeList gameMovesNodeList = rootElement.getElementsByTagName("GameMove");

            for(int i = 0; i < gameMovesNodeList.getLength(); i++) {
                Element gameMoveElement = (Element) gameMovesNodeList.item(i);
                gameMoves.add(buildGameMoveFromElement(gameMoveElement));
            }

        } catch(Exception e) {
            throw new ConfigurationException("There was an error while reading XML file with game moves.", e);
        }
        return gameMoves;
    }

    private static Element createPlayerElement(Document xmlDoc, String mainTagName, Player player) {
        Element playerRoot = xmlDoc.createElement(mainTagName);
        if(player == null) {
            return playerRoot;
        }

        Element name = xmlDoc.createElement("name");
        Element health = xmlDoc.createElement("health");
        Element maxHealth = xmlDoc.createElement("maxHealth");
        Element actionDeck = createCardListElement(xmlDoc, "actionDeck", player.getActionDeck());
        Element discardPile = createCardListElement(xmlDoc, "discardPile", player.getDiscardPile());

        name.setTextContent(player.getName());
        health.setTextContent(String.valueOf(player.getHealth()));
        maxHealth.setTextContent(String.valueOf(player.getMaxHealth()));

        playerRoot.appendChild(name);
        playerRoot.appendChild(health);
        playerRoot.appendChild(maxHealth);
        playerRoot.appendChild(actionDeck);
        playerRoot.appendChild(discardPile);

        return playerRoot;
    }

    private static Element createCardListElement(Document xmlDoc, String mainTagName, List<Card> cards) {
        Element cardRoot = xmlDoc.createElement(mainTagName);
        for (Card card : cards) {
            Element cardId = xmlDoc.createElement("cardId");
            cardId.setTextContent(card.getId());
            cardRoot.appendChild(cardId);
        }
        return cardRoot;
    }

    private static GameMove buildGameMoveFromElement(Element gameMoveElement) {
        List<Card> allCards = CardUtils.loadCards();
        Element playerOne = (Element) gameMoveElement.getElementsByTagName("playerOne").item(0);
        Element playerTwo = (Element) gameMoveElement.getElementsByTagName("playerTwo").item(0);
        Element completedFields = (Element) gameMoveElement.getElementsByTagName("completedFields").item(0);

        return GameMove.builder()
                .playerOne(buildPlayerFromElement(playerOne, allCards))
                .playerTwo(buildPlayerFromElement(playerTwo, allCards))
                .completedFields(buildCompletedFieldsFromElement(completedFields))
                .build();
    }

    private static Player buildPlayerFromElement(Element playerElement, List<Card> allCards) {
        Player player = Player.create();
        Node name = playerElement.getElementsByTagName("name").item(0);
        if(name == null) {
            return null;
        }
        player.setName(name.getTextContent());
        player.setHealth(getFirstIntegerFromElement(playerElement, "health"));
        player.setMaxHealth(getFirstIntegerFromElement(playerElement, "maxHealth"));
        player.setActionDeck(buildCardsFromElement(allCards, playerElement, "actionDeck"));
        player.setDiscardPile(buildCardsFromElement(allCards, playerElement, "discardPile"));
        return player;
    }

    private static List<Card> buildCardsFromElement(List<Card> allCards, Element playerElement, String tagName) {
        List<Card> matchingCards = new ArrayList<>();
        Element actionDeckElement = (Element) playerElement.getElementsByTagName(tagName).item(0);
        NodeList cardIds = actionDeckElement.getElementsByTagName("cardId");
        for (int i = 0; i < cardIds.getLength(); i++) {
            String cardId = cardIds.item(i).getTextContent();
            Card foundCard = allCards.stream().filter(card -> card.getId().equals(cardId))
                    .findFirst().orElseThrow(() -> new ConfigurationException("No card with this ID found"));
            matchingCards.add(foundCard);
        }
        return matchingCards;
    }

    private static List<Integer> buildCompletedFieldsFromElement(Element completedFieldsElement) {
        List<Integer> completedFields = new ArrayList<>();
        NodeList cellNumbers = completedFieldsElement.getElementsByTagName("cellNumber");
        for (int i = 0; i < cellNumbers.getLength(); i++) {
            int cellNumber = Integer.parseInt(cellNumbers.item(i).getTextContent());
            completedFields.add(cellNumber);
        }
        return completedFields;
    }

    private static Integer getFirstIntegerFromElement(Element element, String tagName) {
        return Integer.parseInt(element.getElementsByTagName(tagName).item(0).getTextContent());
    }
}