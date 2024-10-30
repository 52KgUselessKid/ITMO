package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class Whiscash extends Pokemon {
    public Whiscash(String name, int level)
    {
        super(name, level);
        setType(Type.WATER, Type.GROUND);
        setStats(110, 78, 73, 76, 71, 60);

        Rest rest = new Rest();
        Scald scald = new Scald();
        AquaTail aquaTail = new AquaTail();
        RockSlide rockSlide = new RockSlide();

        setMove(rest, scald, aquaTail, rockSlide);
    }

}