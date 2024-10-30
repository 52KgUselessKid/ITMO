package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class Porygon extends Pokemon {
    public Porygon(String name, int level)
    {
        super(name, level);
        setType(Type.NORMAL);
        setStats(65, 60, 70, 85, 75, 40);

        Thunderbolt thunderbolt = new Thunderbolt();
        Confide confide = new Confide();

        setMove(thunderbolt, confide);
    }

}