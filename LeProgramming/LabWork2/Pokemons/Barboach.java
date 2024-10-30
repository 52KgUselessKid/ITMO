package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class Barboach extends Pokemon {
    public Barboach(String name, int level)
    {
        super(name, level);
        setType(Type.WATER, Type.GROUND);
        setStats(50, 48, 43, 46, 41, 60);

        Rest rest = new Rest();
        Scald scald = new Scald();
        AquaTail aquaTail = new AquaTail();

        setMove(rest, scald, aquaTail);
    }

}