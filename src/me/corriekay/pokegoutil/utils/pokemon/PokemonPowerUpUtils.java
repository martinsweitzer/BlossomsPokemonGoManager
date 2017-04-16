package me.corriekay.pokegoutil.utils.pokemon;

import java.util.List;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Settings.Master.MoveSettingsOuterClass.MoveSettings;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass.PokemonSettings;
import POGOProtos.Settings.Master.Pokemon.StatsAttributesOuterClass.StatsAttributes;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;


import me.corriekay.pokegoutil.data.managers.ProfileManager;
import com.pokegoapi.api.player.PlayerProfile;

import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;


/**
 * A Utility class providing several methods to calculate stats and values of Pokémon.
 * Most important ivRating, Duel Ability, Gym Offense and Defense.
 */
public final class PokemonPowerUpUtils 
{

 static Integer[] StardustCosts;

  static {
    StardustCosts = new Integer[] {
new Integer(0),
new Integer(0),
new Integer(200),
new Integer(200),
new Integer(200),
new Integer(200),
new Integer(400),
new Integer(400),
new Integer(400),
new Integer(400),
new Integer(600),
new Integer(600),
new Integer(600),
new Integer(600),
new Integer(800),
new Integer(800),
new Integer(800),
new Integer(800),
new Integer(1000),
new Integer(1000),
new Integer(1000),
new Integer(1000),
new Integer(1300),
new Integer(1300),
new Integer(1300),
new Integer(1300),
new Integer(1600),
new Integer(1600),
new Integer(1600),
new Integer(1600),
new Integer(1900),
new Integer(1900),
new Integer(1900),
new Integer(1900),
new Integer(2200),
new Integer(2200),
new Integer(2200),
new Integer(2200),
new Integer(2500),
new Integer(2500),
new Integer(2500),
new Integer(2500),
new Integer(3000),
new Integer(3000),
new Integer(3000),
new Integer(3000),
new Integer(3500),
new Integer(3500),
new Integer(3500),
new Integer(3500),
new Integer(4000),
new Integer(4000),
new Integer(4000),
new Integer(4000),
new Integer(4500),
new Integer(4500),
new Integer(4500),
new Integer(4500),
new Integer(5000),
new Integer(5000),
new Integer(5000),
new Integer(5000),
new Integer(6000),
new Integer(6000),
new Integer(6000),
new Integer(6000),
new Integer(7000),
new Integer(7000),
new Integer(7000),
new Integer(7000),
new Integer(8000),
new Integer(8000),
new Integer(8000),
new Integer(8000),
new Integer(9000),
new Integer(9000),
new Integer(9000),
new Integer(9000),
new Integer(10000),
new Integer(10000),
new Integer(10000),
new Integer(10000)
};

}


 static Integer[] CandyCosts;

  static {
    CandyCosts = new Integer[] {
	new Integer(0),
new Integer(0),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(1),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(2),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(3),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(4),
new Integer(6),
new Integer(6),
new Integer(6),
new Integer(6),
new Integer(8),
new Integer(8),
new Integer(8),
new Integer(8),
new Integer(10),
new Integer(10),
new Integer(10),
new Integer(10),
new Integer(12),
new Integer(12),
new Integer(12),
new Integer(12),
new Integer(15),
new Integer(15),
new Integer(15),
new Integer(15)
};
}




// https://pokemongo.gamepress.gg/power-up-costs
//
// https://pokemongo.gamepress.gg/cpcalc#/
//
// https://pokeassistant.com/main/ivcalculator?locale=en


  public static long getCandyCost(final Pokemon p) 
  {
  int RetVal = 0;


 int playerLevel = (int) PokemonGoMainWindow.getPoGo().getPlayerProfile().getStats().getLevel();

	   float pokemonLevel = p.getLevel();
	   int pokemonHalfLevels = (int) ( pokemonLevel / 0.5f );


  int numHalfLevelsToLevelUp = ( (playerLevel*2) - pokemonHalfLevels ) ;

  int arrayIndexToStartAt = (pokemonHalfLevels);



 // System.out.println("Pokemon: " + " playerLevel: " + playerLevel + " pokemonLevel: " + pokemonLevel + " pokemonHalfLevels:" + pokemonHalfLevels + " numHalfLevelsToLevelUp: " + numHalfLevelsToLevelUp );

      for (int i = arrayIndexToStartAt; i < (arrayIndexToStartAt + numHalfLevelsToLevelUp); i++) 
	  {
	//  System.out.println("     i: " + i + " " + CandyCosts[i] );
	      RetVal += CandyCosts[i];
	  }

	 //  System.out.println("     RetVal: " + RetVal );
    
	//System.out.println("  " );

	  return RetVal;

  }


  public static long getStarDustCost(final Pokemon p) 
  { 
  int RetVal = 0;


 int playerLevel = (int) PokemonGoMainWindow.getPoGo().getPlayerProfile().getStats().getLevel();

	   float pokemonLevel = p.getLevel();
	   int pokemonHalfLevels = (int) ( pokemonLevel / 0.5f );


  int numHalfLevelsToLevelUp = ( (playerLevel*2) - pokemonHalfLevels ) ;

  int arrayIndexToStartAt = (pokemonHalfLevels);





      for (int i = arrayIndexToStartAt; i < (arrayIndexToStartAt + numHalfLevelsToLevelUp); i++) 
	  {
	      RetVal += StardustCosts[i];
	  }
	     
      return RetVal;
  }
}



