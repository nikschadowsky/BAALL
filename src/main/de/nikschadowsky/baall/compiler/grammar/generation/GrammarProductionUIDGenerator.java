package de.nikschadowsky.baall.compiler.grammar.generation;

/**
 * File created on 01.02.2024
 */
public class GrammarProductionUIDGenerator {

    private static final GrammarProductionUIDGenerator instance = new GrammarProductionUIDGenerator();

    public static GrammarProductionUIDGenerator getInstance() {
        return instance;
    }

    private int currentId = 0;

    private GrammarProductionUIDGenerator() {
    }

    public int nextID() {
        return currentId++;
    }
}
