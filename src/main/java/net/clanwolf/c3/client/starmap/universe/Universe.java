package net.clanwolf.c3.client.starmap.universe;

import java.util.ArrayList;
import java.util.HashMap;

public class Universe {
	public static HashMap<Integer, StarSystem> starSystems = new HashMap<>();
	public static HashMap<String, Faction> factions = new HashMap<>();
	public static ArrayList<Attack> attacks = new ArrayList<>();
	public static HashMap<String, Jumpship> jumpships = new HashMap<>();

	public static Integer currentSeason = 1;
	public static Integer currentRound = 6;
}
