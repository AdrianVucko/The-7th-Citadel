package com.tvz.avuckovic.the7thcitadel.jndi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigurationKey {

    RMI_PORT("rmi.port"), PLAYER_ONE_SERVER_PORT("player.one.server.port"),
    PLAYER_TWO_SERVER_PORT("player.two.server.port"), HOSTNAME("hostname");

    private final String key;
}
